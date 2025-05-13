# 📘 GBS - Greece Business School
![Home Page](src/main/resources/homepage.png)
*An interactive platform designed to enhance learning for business students through multimedia, AI-powered tutoring, and voice interaction.*

Welcome to the official repository of **GBS (Greece Business School)** — an e-learning platform built with **JavaFX** for the front-end and **PostgreSQL** for the database. This platform supports online learning, course management, and user interaction tailored for business school education.

---

## 🧭 Overview
GBS is an all-in-one e-learning platform tailored for business education. Built with JavaFX and PostgreSQL, it offers secure user management, AI-powered tutoring, multimedia courses, and voice interaction tools to enhance learning accessibility and engagement.

---

## 🛠️ Tech Stack

- **Frontend**: JavaFX – Used to create the rich, interactive desktop interface.
- **Backend**: Java – Handles server-side logic, course management, and user authentication.
- **Database**: PostgreSQL – A reliable database for storing user data, courses, and other relevant information.
- **AI Integration**: Google AI Studio API – Powers the AI tutor, providing personalized feedback and learning assistance.
- **Speech Services**: TTS (Text-to-Speech) and STT (Speech-to-Text) – Allows for voice interaction.
- **Build Tool**: Maven – Manages project dependencies and automates builds.
- **IDE**: IntelliJ IDEA – A powerful IDE for Java development.

---

## 🚀 Features

- User authentication (students, tutors, admins)
- ✅ **Course** creation
- ✅ **Video and document** content support
- ✅ **Quiz** module
- ✅ **GPA** tracking for students
- ✅ **Admin dashboard** for managing users
- 🔐 **Secure password** storage using **hashing + salt**
- 🧾 **Dynamic PDF generation** for certificates
- 🤖 **AI Tutor** powered by Google AI Studio API
- 🗣️ **Text-to-Speech (TTS)** for reading course content aloud
- 🎙️ **Speech-to-Text (STT)** for voice input when interacting with the AI

---

## 📦 Installation

### Prerequisites

- Java JDK 17 or higher
- PostgreSQL installed and running
- Maven installed
- JavaFX SDK (if not bundled with your IDE)
- Internet access for AI and voice services
- Linux for TTS and STT

---

### 🧩 Steps to Run the Project

1. **Clone the repository:**
    ```bash
    git clone https://github.com/VoidG4/GBSproject.git
    cd GBSproject
    ```

2. **Set up the PostgreSQL database:**
   - Create a new database named GBSproject in PostgreSQL.
   - Run the schema SQL file (`/db/schema.sql`) to create the necessary tables.
   - Update your database credentials in the `config.properties` file:
    ```properties
    db.url=jdbc:postgresql://localhost:5432/GBSproject
    db.username=your_db_username
    db.password=your_db_password
    ```

3. **Set up AI & Audio Configuration:**
   - Obtain an API Key from Google AI Studio.
   - Add the API key to your `config.properties` file:
    ```properties
    gemini.api.key=your_google_ai_key
    API_URL=https://api.studio.google.com/...
    ```
   - Ensure microphone access is enabled for Speech-to-Text (STT).
   - (Linux) Make sure required audio libraries are installed for TTS/STT.

4. **Set up a Python environment for TTS and STT:**

   - Create a new Python virtual environment called `pyenv`:
     ```bash
     python3 -m venv pyenv
     ```

   - Activate the virtual environment:
     ```bash
     source pyenv/bin/activate
     ```

   - Install necessary libraries for **Text-to-Speech (gTTS)** and **Speech Recognition (SpeechRecognition)**:
     ```bash
     pip install gTTS SpeechRecognition
     ```

   - Deactivate the environment when done:
     ```bash
     deactivate
     ```

5. **Build and run the application:**
    ```bash
    mvn clean install
    mvn javafx:run
    ```

   Alternatively, run directly from IntelliJ IDEA by opening the project, ensuring JavaFX is set up, and running `GBSApplication.java`.

---

## 📚 Libraries & Dependencies

- **JavaFX**
- **PostgreSQL JDBC Driver**
- **JUnit 5**
- **Json**
- **Google AI Studio API**
- **gTTS** Python library
- **SpeechRecognition** Python library
- **iTextPDF**

---

## 🔐 Security

- User passwords are **hashed and salted** before being stored in the PostgreSQL database for protection.
- The application follows industry standards for secure user authentication and data storage.

---

## 🔑 Configuration Security

- Sensitive information such as **API keys**, **database credentials**, and **URLs** are stored securely in `config.properties`.
- Consider using environment variables or secret managers in production for enhanced security.

---

## ✅ Testing

- Unit tests are provided using **JUnit 5**. To run tests:
    ```bash
    mvn test
    ```

Tests are located in the `src/test/java/com/gbs/gbsproject` directory.

---

## 📖 User Manual

A complete user guide is available for GBS, covering account setup, course enrollment, AI tutor use, and voice interaction.

📄 [User Manual](./Greece_Business_School_User_Manual.pdf)

---

## 👨‍💻 Developer

- **Name**: VoidG4
- **GitHub**: [VoidG4](https://github.com/VoidG4)

---

## 📝 License
This project is for educational purposes only. Redistribution or commercial use requires explicit permission.

