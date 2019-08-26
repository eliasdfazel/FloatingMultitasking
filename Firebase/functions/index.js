'use strict';

const functions = require('firebase-functions');
const nodemailer = require('nodemailer');
const util = require('util');
const fs = require('fs');

const admin = require('firebase-admin');
admin.initializeApp();

const runtimeOptions = {
    timeoutSeconds: 313,
}

/*[List All Users]*/
exports.retrieveAllUsersData = functions.runWith(runtimeOptions).https.onRequest((req, res) => {
    listAllUsers();    

    return "Sending Email To Users";
});

function listAllUsers(nextPageToken) {
    // List batch of users, 1000 at a time.
    admin.auth().listUsers(1000, nextPageToken)
      .then(function(listUsersResult) {
        listUsersResult.users.forEach(function(userRecord) {
            var userData = userRecord.toJSON();
            console.log('User Data :::' + userRecord.displayName + " | " + userData.email);
        });
        if (listUsersResult.pageToken) {
          // List next batch of users.
          listAllUsers(listUsersResult.pageToken);
        }
    })
    .catch(function(error) {
        console.log('Error listing users:', error);
    });
}  

/*[Send New Promotion Email To Users]*/
exports.sendPromotionalEmail = functions.runWith(runtimeOptions).https.onRequest((req, res) => {
    
    /*Run Send Email Through Loop Of Target Users*/ 
    var emailStringArray = ['eias.fazel@gmail.com'];
    for (var i = 0; i < emailStringArray.length; i++) {
        console.log(emailStringArray[i]);
        
        sendEmailToUsers(emailStringArray[i])
    }

    res.status(200).send(emailStringArray);
    return "Sending Email To Users";
});

const gmailEmail = "GeeksEmpireInc@gmail.com";//Email Will Send As "Support@GeeksEmpire.net" Cause It is Default
const gmailPassword = "bnwliueajaxordbz";
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

async function sendEmailToUsers(email) {

    const mailOptions = {
        from: `#FloatIt Community 🎈`,
        to: email,
    };

    //The user subscribed to the newsletter.
    mailOptions.subject = `Check Out Floating Widgets | #FloatIt 🎈 `;
    //mailOptions.text = `Hey ${email || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
    var templateHtml = fs.readFileSync('EmailContent.html',"utf-8").toString();
    mailOptions.html = templateHtml
    await mailTransport.sendMail(mailOptions, function(error, info){
        if(error){
            return console.log('Error: ' + error + ' | ' + email);
        }
        console.log('Email Sent: ' + info.response + ' | ' + email);
    });

    return "Email Sent";
}