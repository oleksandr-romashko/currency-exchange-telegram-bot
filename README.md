# Currency Exchange (telegram-bot)

Telegram bot application for obtaining exchange rates of three Ukrainian banks using their API ([National Bank of Ukraine](https://bank.gov.ua/en/markets/exchangerates), [PrivatBank (non-cash rates)](https://en.privatbank.ua/) and [Monobank](https://www.monobank.ua/?lang=en)) and displaying this information to the User in Telegram.

<img src="docs/img/app-screenshot.png" width="55%" alt="Application screenshot">

## Prerequisites

* Installed Java 14 JDK to compile java code
* Telegram bot authentication token to authorize the bot and send requests to the Bot API (can be obtained from the [@BotFather](https://t.me/BotFather))

## How to build

1. Create Telegram bot (e.g. with help of [@BotFather](https://t.me/BotFather)) and obtain token.
2. Add this token to `bot.token` property in `src/main/resources/application.properties` file.
3. Use `./gradlew fatJar` command in terminal to generate Java archive file with dependencies. Jar file will be created in `build/libs` folder.

## How to run

In terminal in projects root use `java -jar ./build/libs/CurrencyExchange-TelegramBot-all-1.0-SNAPSHOT.jar` command. Application will start.

To terminate application use `Ctrl + C` shotrkey.

## Available functionality

* get_info - gets information about currencies rates for selected currencies and banks
* settings - settings menu - all user settings are stored in `users.json` file in `src/main/resources` directory and may be reused after application restart.
* number_of_decimal_places - setting, which changes the number of displayed decimal places
* currency - setting, which changes currencies to display
* notification_time - setting, which changes daily notification time or turn off notification
* bank - setting, which changes banks to display

## Bot implementation example

[@currency_exchange_telegram_bot](https://t.me/currency_exchange_telegram_bot) is an example of a Currency Exchange telegram bot implementation.

Bot settings, which may be set for a bot using [@BotFather](https://t.me/BotFather):

* Name: Currency Exchange
* About: Currency Exchange Bot will help you track current exchange rates for selected UA banks and currencies
* Description: Welcome. This bot will help you track current exchange rates for selected banks and currencies. Please use /start command to start using your bot.
* Description picture: ???? no description picture
* Botpic: ???? has a botpic ([botpic image example](docs/img/botpic.png))
* Commands: 1 command
  * start - send start message
