const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendChatNotification = functions.database.ref('/Chats/{userID}/{friendID}/{messageID}/message').onWrite((change, context) => 
{
	const userID = context.params.userID;
	const friendID = context.params.friendID;
	const messageID = context.params.messageID;
	console.log('Change: ', change.after.val());
	const messageBody = change.after.val();
	// User deleted message
	//if (!change.after.val()) {
      //  return console.log('User ', followerUid, 'un-followed user', followedUid);
     // }
	console.log('New message from UID: ', friendID, ' for user UID: ', userID, ' with message ID: ', messageID);

	const getDeviceTokensPromise = admin.database().ref(`/Users/${userID}/notificationTokens`).once('value');
	
	const getFriendProfilePromise = admin.auth().getUser(friendID);
	
	//const getMessagePromise = admin.database().ref(`/Chats/${userID}/${friendID}/${messageID}/message`).once('value');
	
	let tokenSnapshot;
	let tokens;
	console.log('Before Promise function');
	return Promise.all([getDeviceTokensPromise, getFriendProfilePromise]).then(results => {
		console.log('In Promise function');
		
		console.log('Results',results);
		tokenSnapshot = results[0];
		const friend = results[1];
		console.log('Message: ', messageBody);
		
		// check if tokenSnapshot has children
		if(!tokenSnapshot.hasChildren())
			return console.log('Es gibt keine Tokens an die gesendet werden kann');
		
		console.log(`Es gibt `, tokenSnapshot.numChildren(), ' Token an die gesendet werden kann.');
		console.log('Fetched friend profile ', friend);
		
		const payload = {
			notification: {
				title: `You have a new message from ${friend.displayName}`,
				body: messageBody,
				icon: friend.photoURL
			}
		};
		
		tokens = Object.keys(tokenSnapshot.val());
		
		return admin.messaging().sendToDevice(tokens, payload);
	}).then((response) => {
		
		// For each message check if there was an error.
		console.log('Response: ', response);
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokenSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
		
		return Promise.all(tokensToRemove);
	});
	
	//doNotification("Created: ", event);
});
/*
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

*/