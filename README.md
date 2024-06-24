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

Android SDK: For building and deploying the Android application.

Firebase Authentication: For secure OTP-based phone number authentication, saving sign-in users information and help in store chats messages of users.

Usage Flow:

User opens the WhatsApp Clone app and splash screen will display with the app name and logo.

![Screenshot_2024-06-23-22-04-22-475_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/1dce9510-f1b4-4e5a-bd7f-ab0d832cbf90)

This screen will appear for the first time when the user have installed an app.

![Screenshot_2024-06-20-20-46-47-797_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/1b17b4dd-409a-4d17-9469-b0ab1d0720ff)

User can choose their own country language but for now this app served only English language

![Screenshot_2024-06-20-20-47-10-127_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/5c32c335-4ac9-4379-baaf-6809ae1e4d25)

User logs in using their phone number and verifies with an OTP.

![Screenshot_2024-06-20-20-49-08-103_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/17feb557-c68f-4a53-82ab-2656ab1f9ce3)

![Screenshot_2024-06-20-21-01-10-553_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/2e064d30-f890-4d50-8caf-45308487aa81)

User sets up their profile by entering their name(mandatory) and setting a profile picture by capturing from Camera or by uploading from Gallery(optional).

![Screenshot_2024-06-20-21-01-17-183_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/c6187ac2-f77f-470c-b7c8-77e0241b34c2)

User can start individual or group chats with contacts who are signed in to the app.

![IMG_20240624_161510 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/f9b891e4-71ef-4b6e-abba-ea67039827a5)

User can upload a only status image, for now user can not upload status video also user can upload only one image at a time otherwise it overwrites previous status.

![IMG_20240624_222642 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/f74dae71-a594-4d76-bba4-56c0d01a0f83)

User can simulate audio or video calls with fake contacts from the call screen.

![Screenshot_2024-06-24-22-20-52-077_com example whatsappclone 1](https://github.com/ArpitAswal/ChattingApplication/assets/87036588/a183c1e9-fd06-4eff-a864-d8751409d9ed)
