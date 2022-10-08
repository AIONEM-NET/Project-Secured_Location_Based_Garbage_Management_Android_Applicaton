
// https://console.firebase.google.com/u/1/project/location-garbage/database

const firebaseConfig = {
    apiKey: "AIzaSyBGpub89ll4zWp0eV0NU3jC8-Gqo355D-s",
    authDomain: "location-garbage.firebaseapp.com",
    databaseURL: "https://location-garbage-default-rtdb.firebaseio.com",
    projectId: "location-garbage",
    storageBucket: "location-garbage.appspot.com",
    messagingSenderId: "869302587241",
    appId: "1:869302587241:web:1acd7097147cbf596ab7f8",
    measurementId: "G-B66ELXZD6E"
};

const fApp = firebase.initializeApp(firebaseConfig);
const fAuth = firebase.auth();
const fDatabase = firebase.database();
const fStorage = firebase.storage();
const fAnalytics = firebase.analytics();

const userID = window.localStorage.getItem("userID");
const userEmail = window.localStorage.getItem("userEmail");
const userName = window.localStorage.getItem("userName");
const userAccount = window.localStorage.getItem("userAccount");
