WhatsApp Clone is a mobile messaging application that mimics the core functionalities and user interface of the original WhatsApp. The app provides a robust platform for secure messaging, group chats, and status updates, along with an OTP-based phone number authentication system. While still in development, the app aims to deliver a seamless user experience with essential communication features.

Features:

User Authentication:
Phone Number Login-> Users can sign in using their phone number.
OTP Authentication-> Secure OTP verification ensures safe and reliable user authentication.

Profile Setup:
After authentication, users can set up their profile by entering their name and uploading a profile picture.

Chat Functionality:
Individual Chats-> Users can chat with individual contacts who are also signed in to the app.
Group Chats-> Users can create groups and chat with multiple users simultaneously.

Status Feature:
Users can upload a single status image at a time.
Adding a new status image overwrites the previous one.
The current implementation does not support viewing statuses in a new frame or uploading videos.

Call Screen:
Fake Contacts-> Users can simulate audio or video calls with fake contacts.
Call Simulation-> Users can select the number of contacts and choose between audio and video call options to simulate calls.

Splash Screen:
A smooth transition splash screen displays the app name and logo upon launching.

Contact Management:
Users can only see contacts who have signed in to the app.
Custom contacts can be created but do not support interactive chatting.

Future Updates:

Enhanced Status Feature: Plans to support multiple statuses, view statuses in a new frame, and allow video uploads.
Search Functionality: To help users quickly find contacts, messages, or groups.
Settings Feature: To provide users with more customization options and improve user-friendliness.

Technologies Used:
Kotlin: Primary programming language for app development.
Firebase Authentication: For secure OTP-based phone number authentication, saving sign-in users information and help in store chats messages of users.
Android SDK: For building and deploying the Android application.
