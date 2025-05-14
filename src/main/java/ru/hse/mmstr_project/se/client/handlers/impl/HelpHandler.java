package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;

import java.util.Optional;

@Component
public class HelpHandler implements CommandHandler {

    private static final String HELP_TEXT = """
            /start
            Активирует бота, регистрирует пользователя.
            
            /help
            Предоставляет список всех команд и их функционал.
            
            
            Обработка контактов:
            
            /add_contact
            Начинает процесс добавления нового контакта. Бот отвечает с уникальным идентификатором (ID) для нового контакта.
           
            /set_name_for_contact {id} {Имя}
            Устанавливает имя.

            /set_notification_methods {id} list[{Способ уведомления
            Устанавливает способ уведомления (SMS, Telegram, Email).

            /set_contact_data {id} {Способ уведомления} {Данные}
            Устанавливает необходимые данные (например, номер телефона, email или Telegram-ID).
            Телеграмм: без @
            Телефон: 7*****

            /remove_contact {id}
            Удаляет контакт из списка доверенных лиц.
         
            /list_contacts
            Выводит список всех добавленных контактов.
            ________________________________
            
            Обработка сценариев:
            
            /add_scenario (опц.) {Название}
            Начинает процесс создания нового сценария. Бот выдает ID нового сценария. Можно сразу указать имя.
            
            /set_scenario_name {id} {Название}
            Устанавливает название для сценария.

            /set_scenario_text {id} или {name} {Текст рассылки}
            Устанавливает текст рассылки для сценария.
            
            /set_check_times {id} или {name} list({Время проверки})
            Устанавливает время автоматической проверки состояния пользователя для сценария.
            Позволяет указать несколько проверок.
            
            /set_allowed_delay {id} или {name}  {Время}
            Устанавливает разрешенную задержку в ответе (в минутах) на проверочное сообщение для сценария.
            
            /set_verification_message {id} или {name} {Сообщение}
            Устанавливает проверочное сообщение для сценария.

            /set_contacts_to_scenario {id или {name} list({id контакта})
            Добавляет контакты к сценарию.

            /remove_scenario {id} или {name}
            Удаляет сценарий по названию.

            /activate_scenario {id} или {name}
            Активирует указанный сценарий.

            /deactivate_scenario {id} или {name}
            Деактивирует указанный сценарий.

            /list_scenarios
            Выводит список доступных сценариев.
            ________________________________
            
            Обработка алертов:
            /confirm
            Подтверждает стабильное состояние, завершая текущий сценарий.

            /delay (опц.) {min}
            Откладывает текущую проверку на указанное количество минут, по умолчанию 5 минут.

            /get_next_alert
            Показывает время срабатывания ближайшего сценария

            /skip_next_alert
            Показывает время срабатывания ближайшего сценария
            ________________________________

            Быстрый старт:
            `/start`
            `/add\\_contact`
            `/set\\_contact\\_data 1 tg ?`
            `/add\\_scenario ahaha`
            `/set\\_contacts\\_to_scenario ahaha 1`
            `/set\\_check\\_times ahaha 2025-05-16T00:00:01Z`
            `/sos`
            """;

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        return Optional.of(HELP_TEXT);
    }

    @Override
    public String getCommand() {
        return "/help";
    }
}
