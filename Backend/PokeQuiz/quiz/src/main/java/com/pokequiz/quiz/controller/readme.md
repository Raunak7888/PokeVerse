# 🧠 PokeQuiz API Documentation

Welcome to the **PokeQuiz API**! This document provides an overview of the available endpoints across multiple controllers, making it easy to interact with the quiz system.

## 📌 Table of Contents

- [Game Controller](#game-controller)
- [Quiz Analysis Controller](#quiz-analysis-controller)
- [Quiz Attempt Controller](#quiz-attempt-controller)
- [Quiz Controller](#quiz-controller)
- [Quiz Session Controller](#quiz-session-controller)
- [Room Controller](#room-controller)
- [WebSocket Controller](#websocket-controller)

---

## 🎮 Game Controller

| Method | Endpoint                                  | Description                                             |
| ------ | ----------------------------------------- | ------------------------------------------------------- |
| GET    | `/game/hello`                             | Returns a hello message.                                |
| GET    | `/game/all`                               | Fetches all quizzes.                                    |
| GET    | `/game/difficulty/all`                    | Fetches quizzes by difficulty.                          |
| GET    | `/game/region/all`                        | Fetches quizzes by region.                              |
| GET    | `/game/quiztype/all`                      | Fetches quizzes by quiz type.                           |
| GET    | `/game/region/difficulty/quiztype/all`    | Fetches quizzes by region, difficulty, and type.        |
| GET    | `/game/random`                            | Fetches random quizzes with a limit.                    |
| GET    | `/game/random/difficulty/region/quiztype` | Fetches random quizzes by region, difficulty, and type. |
| GET    | `/game/gradually`                         | Returns quizzes with gradually increasing difficulty.   |

---

## 📊 Quiz Analysis Controller

| Method | Endpoint                       | Description                             |
| ------ | ------------------------------ | --------------------------------------- |
| POST   | `/quiz-analysis/{sessionId}`   | Analyzes a quiz session.                |
| GET    | `/quiz-analysis/{sessionId}`   | Retrieves the analysis for a session.   |
| GET    | `/quiz-analysis/user/{userId}` | Retrieves analyses for a specific user. |

---

## 📑 Quiz Attempt Controller

| Method | Endpoint                     | Description                                        |
| ------ | ---------------------------- | -------------------------------------------------- |
| POST   | `/quiz-attempt/submit`       | Submits a quiz attempt.                            |
| GET    | `/quiz-attempt/session`      | Retrieves attempts by session.                     |
| GET    | `/quiz-attempt/session/time` | Retrieves attempts by session within a time range. |
| GET    | `/quiz-attempt/question`     | Retrieves attempts by question ID.                 |

---

## 📚 Quiz Controller

| Method | Endpoint                                  | Description                                             |
| ------ | ----------------------------------------- | ------------------------------------------------------- |
| GET    | `/quiz/hello`                             | Returns a hello message.                                |
| POST   | `/quiz/add`                               | Adds a new quiz.                                        |
| GET    | `/quiz/all`                               | Fetches all quizzes.                                    |
| GET    | `/quiz/difficulty/all`                    | Fetches quizzes by difficulty.                          |
| GET    | `/quiz/region/all`                        | Fetches quizzes by region.                              |
| GET    | `/quiz/quiztype/all`                      | Fetches quizzes by quiz type.                           |
| GET    | `/quiz/region/difficulty/quiztype/all`    | Fetches quizzes by region, difficulty, and type.        |
| GET    | `/quiz/random`                            | Fetches random quizzes with a limit.                    |
| GET    | `/quiz/random/difficulty/region/quiztype` | Fetches random quizzes by region, difficulty, and type. |

---

## 📅 Quiz Session Controller

| Method | Endpoint                           | Description                              |
| ------ | ---------------------------------- | ---------------------------------------- |
| POST   | `/quiz-session/create`             | Creates a new quiz session.              |
| GET    | `/quiz-session`                    | Fetches all quiz sessions.               |
| PUT    | `/quiz-session/update/{sessionId}` | Updates a quiz session's status.         |
| GET    | `/quiz-session/{sessionId}`        | Fetches a specific quiz session by ID.   |
| GET    | `/quiz-session/active/{userId}`    | Retrieves the active session for a user. |

---

## 🏠 Room Controller

| Method | Endpoint         | Description                          |
| ------ | ---------------- | ------------------------------------ |
| POST   | `/room/create`   | Creates a new game room.             |
| POST   | `/room/join`     | Allows a player to join a game room. |
| GET    | `/room/{roomId}` | Retrieves information about a room.  |
| GET    | `/room/all`      | Fetches all available rooms.         |

---

## 🔔 WebSocket Controller

| Method                    | Endpoint                        | Description                                                |
| ------------------------- | ------------------------------- | ---------------------------------------------------------- |
| `/chat/{roomId}`          | Sends a chat message to a room. | Broadcasts a user's chat message to all participants.       |
| `/start/{roomId}`         | Starts the game in a room.      | Initiates the game and prepares the room for quiz questions.|
| `/game/{roomId}/{hostId}` | Begins quiz questions.          | Starts the quiz questions for a given room and host.        |
| `/game/answer/validation` | Validates quiz answers.         | Checks the submitted answers and updates scores accordingly.|
| `/game/stats/{roomId}`    | Sends game statistics.          | Broadcasts live game statistics to all room participants.   |

---

## 🚀 Usage

Ensure the API is running and use the provided endpoints to interact with the PokeQuiz system. You can test these endpoints using tools like **Postman**, **cURL**, or directly from your frontend application.


