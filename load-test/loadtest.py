import psycopg2
from psycopg2 import pool
import time
from datetime import datetime, timedelta, timezone
import json
from concurrent.futures import ThreadPoolExecutor
import threading
import argparse

DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "database": "postgres",
    "user": "user",
    "password": "password"
}

connection_pool = pool.ThreadedConnectionPool(
    minconn=1,
    maxconn=20,
    **DB_CONFIG
)

thread_local = threading.local()

def get_connection():
    if not hasattr(thread_local, "connection"):
        thread_local.connection = connection_pool.getconn()
    return thread_local.connection

def save_client(conn, telegram_id, chat_id, friends_list):
    try:
        cursor = conn.cursor()
        cursor.execute(
            "INSERT INTO clients (telegramId, chatId, listOfFriends) VALUES (%s, %s, %s) ON CONFLICT (telegramId) DO NOTHING",
            (telegram_id, chat_id, json.dumps(friends_list))
        )
        conn.commit()
        cursor.close()
    except Exception as e:
        conn.rollback()
        print(f"Error saving client: {e}")

def save_scenarios_batch(conn, scenarios):
    try:
        cursor = conn.cursor()

        for scenario in scenarios:
            cursor.execute("""
            INSERT INTO scenario
            (text, clientId, friendIds, firstTimeToActivate, listTimesToActivate,
            allowedDelayAfterPing, okFromAntispam, textToPing)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            """, (
                scenario["text"],
                scenario["client_id"],
                scenario["friend_ids"],
                scenario["first_time_to_activate"],
                scenario["list_times_to_activate"],
                scenario["allowed_delay_after_ping"],
                scenario["ok_from_antispam"],
                scenario["text_to_ping"]
            ))

        conn.commit()
        cursor.close()
    except Exception as e:
        conn.rollback()
        print(f"Error saving scenarios: {e}")

def load_test(delay_ms):
    delay_sec = delay_ms / 1000.0

    conn = get_connection()

    try:
        save_client(conn, "1", 11, [])
    except:
        pass

    count = 0
    while True:
        try:
            time.sleep(delay_sec)

            if count % 100 == 0:
                print(f"Ahaha: {count} (Thread: {threading.current_thread().name})")

            scenarios = []
            now = datetime.now(timezone.utc)

            for i in range(10):
                first_time = now + timedelta(seconds=17 + 3 * i)
                second_time = now + timedelta(seconds=32 + 2 * i)

                scenario = {
                    "text": "aahahhahahah",
                    "client_id": 1,
                    "friend_ids": [],
                    "first_time_to_activate": first_time,
                    "list_times_to_activate": [first_time, second_time],
                    "allowed_delay_after_ping": 1,
                    "ok_from_antispam": True,
                    "text_to_ping": "heh"
                }
                scenarios.append(scenario)

            save_scenarios_batch(conn, scenarios)
            count += 1

        except KeyboardInterrupt:
            print(f"Test interrupted by user (Thread: {threading.current_thread().name})")
            break
        except Exception as e:
            print(f"Error in load test: {e} (Thread: {threading.current_thread().name})")

def multi_threaded_load_test(num_threads=1, delay_ms=300):
    with ThreadPoolExecutor(max_workers=num_threads) as executor:
        futures = []
        for _ in range(num_threads):
            futures.append(executor.submit(load_test, delay_ms))

        for future in futures:
            try:
                future.result()
            except Exception as e:
                print(f"Thread execution error: {e}")

if __name__ == "__main__":
    try:
        multi_threaded_load_test(1, 100)
    except KeyboardInterrupt:
        print("Test interrupted by user")
    finally:
        if 'connection_pool' in globals():
            connection_pool.closeall()
            print("All connections closed")