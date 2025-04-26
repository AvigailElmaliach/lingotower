# LingoTower Client

**PROPRIETARY SOFTWARE - ALL RIGHTS RESERVED**

A desktop application for language learning, focusing on Hebrew-English vocabulary acquisition with interactive features for learners at all levels.

## Overview

LingoTower is an educational platform designed to help users learn languages through various interactive methods. This client-side application connects to the LingoTower backend service, providing a comprehensive desktop interface for language learning activities.

## Features

- **User Authentication** - Secure login and registration
- **Interactive Word Learning** - Learn words with translations and example sentences
- **Category-based Learning** - Organize vocabulary by themed categories
- **Daily Word Feature** - Expand vocabulary with a new word each day
- **Quiz System** - Test knowledge with vocabulary quizzes and sentence completion exercises
- **Progress Tracking** - Visual statistics and learning recommendations
- **Built-in Translator Tool** - Translate text on demand
- **Admin Panel** - Manage users, content, and system settings

## Prerequisites

- Java Development Kit (JDK) 17 or newer
- Maven 3.6+
- LingoTower Backend Service running at `http://localhost:8080`

## Installation

1. Navigate to the client folder:
   bash
   cd LingotowerClient
   ```

2. Build the project:
   bash
   mvn clean install
   ```

3. Run the application:
   bash
   mvn javafx:run
   ```

## Technologies

- JavaFX 17+
- Spring RestTemplate
- Maven
- SLF4J with Logback
- JWT-based authentication

---

© 2025 LingoTower - All Rights Reserved. Unauthorized use, copying, or distribution is prohibited.