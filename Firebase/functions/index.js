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
exports.sendPromotionalEmail = functions.runWith(runtimeOptionsSendingEmails).https.onRequest((req, res) => {

    /*Run Send Email Through Loop Of Target Users*/ 
    var emailStringArray = ['m.keleshev@gmail.com',
    'jamie.bacolod@gmail.com',
    'dasuttam654321@gmail.com',
    'krzysiekrozmyty@gmail.com',
    'kafuhr77@gmail.com',
    'tiful201810@gmail.com',
    'ola.d.sether@gmail.com',
    'sel_kah_28@hotmail.com',
    ];
    for (var i = 0; i < emailStringArray.length; i++) {        
        sendEmailToUsersFloatIt(emailStringArray[i])
    }

    res.status(200).send(emailStringArray);
    return "Sending Email To Users";
});

const gmailEmail = "GeeksEmpire.Net@gmail.com";//Email Will Send As Default Alias
//GeeksEmpireInc@gmail.com
//GeeksEmpire.Net@gmail.com
const gmailPassword = "dgzkjtnwmhsxydeb";//AppPassword
//omdcpzvbpxdcmwyq
//dgzkjtnwmhsxydeb
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

var emailSentCounter = 0;
async function sendEmailToUsersFloatIt(email) {
    console.log("Sending Email To ::: " + email);
    const mailOptions = {
        from: '#FloatIt Community 🎈 @GeeksEmpire.net',
        //Pin Pics On Map PinPicsOnMap@GeeksEmpire.net
        to: email,
    };

    //The user subscribed to the newsletter.
    mailOptions.subject = '#FloatIt 🎈 To Become Master Of Multitasking | #Android';
    //mailOptions.text = `Hey ${email || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
    var templateHtml = fs.readFileSync('FloatIt.html',"utf-8").toString();
    mailOptions.html = templateHtml
    await mailTransport.sendMail(mailOptions, function(error, info){
        if(error){
            return console.log('Error: ' + error + ' | ' + email);
        }
        emailSentCounter = emailSentCounter + 1;
        console.log('Email Sent: ' + info.response + ' | ' + emailSentCounter + '. ' + email);
    });

    return "Email Sent";
}

async function sendEmailToUsersFloatingWidgets(email) {
    console.log("Sending Email To ::: " + email);
    const mailOptions = {
        from: '#FloatIt Community 🎈 @GeeksEmpire.net',
        to: email,
    };

    //The user subscribed to the newsletter.
    mailOptions.subject = 'Check Out Floating Widgets | #FloatIt 🎈 ';
    //mailOptions.text = `Hey ${email || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
    var templateHtml = fs.readFileSync('FloatIt.html',"utf-8").toString();
    mailOptions.html = templateHtml
    await mailTransport.sendMail(mailOptions, function(error, info){
        if(error){
            return console.log('Error: ' + error + ' | ' + email);
        }
        emailSentCounter = emailSentCounter + 1;
        console.log('Email Sent: ' + info.response + ' | ' + emailSentCounter + '. ' + email);
    });

    return "Email Sent";
}