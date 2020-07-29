# The following is the readme document of ”Chatting”- An Instant Messaging Application.
Instant Messaging is one of the primary applications that are most frequently used. Instant Messaging feature allows the user to send messages over the internet by detecting the presence of users available online.In this project, we have constructed a simple IM application to enhance the experience of mobile texting in android system.In this work we have managed to develop a complete application supporting online chatting between two users and their ability to see the chat log to see the latest messages received from different users present in their contact list.
## Design
The application has been developed using the following:
1) Android Studio to do the Kotlin coding
2) Firebase to do the authentication services as well as to access the database.
The functioning of the application can be summarized as:
### Registration Activity
When a user registers for the first time, he has to provide a user name, profile image, email id and password. Since the email and password will be used for every time logging into the application, hence these two are authenticated by the Firebase to check if they have been inserted using the predefined format of the Firebase. Here, authentication validation at the Firebase side includes checking the presence of ’@’ at the email address and the password length of at least six characters. Registration is prevented if the email id provided matches with the email id of some other existing user or missing of ’@’ sign or if the password length is less than six characters. Before setting up the Firebase authentication, it needs to be configured with the Android application that we have been working on. On successful registration, the user id is created for that user at the Firebase server side. This user id is later used to refer that user while storing the details of the user like his profile image, username and the user id itself in the Firebase database as well to perform activities like fetching the latest messages from his chat log or starting a new chat with some other user.
![alt text](RegistrationActivity.png "RegistrationActivity")
### Log In Activity
Users who already have an account, click on the ’Already have an account?’ option from where they will be directed to the Log In page. In the Log In page, when the user enters his email id and password to log in, the same user authentication will be done at the Firebase. Failure of authentication will prevent the user from logging into the application. Failure can occur if
the user id or the password entered is not correct i.e, not matches with what is there in the Firebase.
![alt text](LogIn.png "LogIn Activity")
### CurrentUserActivity
After logging into the application, the user is directed to the chat log page where he can see the latest messages that he has received from the users in his contact list. This is achieved in the following manner. The user’s chat partner id which is retrieved from the chat message activity is matched to see if it matches with the from id or the to id that is associated with each of the chat messages exchanged between the user and hispartner. The from id and the toId are stored in the latest-messages node of the Firebase realtime database created for this application from which it is fetched and matched with the chat partner id. Based on this chat partner id(either from id or to id), the latest messages associated with that id is shown in the recyclerview.
![alt text](CurrentUserActivity.png "CurrentUserActivity")
### ChatLog Activity
When the user clicks on one of the contacts in his contact, the chat log of the user with that contact opens up. Here, the user sends and receives messages from the selected contact. Whether the chat messages are sent to the user or by the user are determined from the from id and to id referred from the user-messages node of the database. The last message exchanged
in this chat log are stored in the latest-messages node of the Firebase database node with the corresponding toId and fromId which are used to display the contact user profile along with the latest message exchanged with that user in the chat log.

![alt text](ChatLog.png "ChatLog Activity")
### Application Flow
Below is the application flow:

![alt text](ApplicationFlow.png "ApplicationFlow")

### Future Works

1) Group chat activity
2) Ability to attach media for transference using the application
3) Providing the activity status of each user in the chatlog title bar
4) Showing the delivery and read receipt of each message transferred between the user and the recipients with whom he
had interacted.
5) Providing the feature of direct scanning and sending of documents in pdf format while using this application.
6) Providing the feature of audio and video calling.

The above application has been developed by understanding the concepts from the following source:
https://www.letsbuildthatapp.com/course/Podcasts


       