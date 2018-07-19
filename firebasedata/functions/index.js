const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationOnCreate = functions.database.ref('/Chats/1R6qJhwirNQWt2TlvBw5rTmT7O32/NpkHz7vdgobCO3bFcl0tz3AGZXN2').onCreate(event => 
{
	doNotification("Created: ", event);
});

exports.sendNotificationonUpdate = functions.database.ref('/Chats/1R6qJhwirNQWt2TlvBw5rTmT7O32/NpkHz7vdgobCO3bFcl0tz3AGZXN2').onUpdate(event => 
{
	doNotification("Updated: ", event);
});
exports.sendNotificationOnDelete = functions.database.ref('/Chats/1R6qJhwirNQWt2TlvBw5rTmT7O32/NpkHz7vdgobCO3bFcl0tz3AGZXN2').onDelete(event => 
{
	doNotification("Deleted: ", event);
});
exports.sendNotificationOnWrite = functions.database.ref('/Chats/1R6qJhwirNQWt2TlvBw5rTmT7O32/NpkHz7vdgobCO3bFcl0tz3AGZXN2').onWrite(event => 
{
	doNotification("Written: ", event);
});

function doNotification(message, event){
	const payload = {
		notification: {                      
			title: "New Notification",    
			body: message            
			}    
		};       
	var topic="TestTopic";  
	return admin.messaging().sendToTopic(topic, payload).then(function(response){      
		console.log("Successfully sent message", response);           
	}).catch(function(error){  
		console.log("Error sending message", error);       
	}); 
}

