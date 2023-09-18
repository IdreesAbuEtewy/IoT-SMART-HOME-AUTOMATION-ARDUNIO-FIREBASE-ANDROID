// Your web app's Firebase configuration
const firebaseConfig = {
	apiKey: "AIzaSyArxkvI9qsqsPoqqpTXnpeg17zYmvn5KAU",
	authDomain: "smarthome-main.firebaseapp.com",
	databaseURL: "https://smarthome-main-default-rtdb.firebaseio.com",
	projectId: "smarthome-main",
	storageBucket: "smarthome-main.appspot.com",
	messagingSenderId: "732237109857",
	appId: "1:732237109857:web:9179344558955f277de820",
	measurementId: "G-25B7KKX1YH"
  };
  // Initialize Firebase
firebase.initializeApp(firebaseConfig);

$(document).ready(function() {
	var database = firebase.database();
	var portStatus = {};

	for (var i = 1; i <= 6; i++) {
		(function(portIndex) {
			var portPath = "/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port" + i;
			var firebaseRef = database.ref(portPath);

			firebaseRef.on("value", function(snapshot) {
				portStatus[portIndex] = snapshot.val();
				updateToggleButton(portIndex);
				updateSchedulerValues(portIndex);
			});

			$(".toggle-btn" + portIndex).click(function() {
				var currentState = portStatus[portIndex];
				var newState = currentState === "1" ? "0" : "1";
				firebaseRef.set(newState);
			});

			$(".apply-btn").click(function() {
				var portIndex = $(this).data("port");
				var days = [];
				$("#port" + portIndex + " .scheduler input[type='checkbox']:checked").each(function() {
					days.push($(this).next().text());
				});
				var onTime = $("#port" + portIndex + "-on-time").val();
				var offTime = $("#port" + portIndex + "-off-time").val();
				updateScheduler(portIndex, days, onTime, offTime);
			});


			function updateToggleButton(portIndex) {
				if (portStatus[portIndex] === "1") {
					$("#unact" + portIndex).hide();
					$("#act" + portIndex).show();
				} else {
					$("#unact" + portIndex).show();
					$("#act" + portIndex).hide();
				}
			}

			function updateScheduler(portIndex, days, onTime, offTime) {
				var daysPath = "/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/days";
				var formattedDays = JSON.stringify(days);

				firebase.database().ref(daysPath).set(formattedDays);
				firebase.database().ref("/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/ontime").set(onTime);
				firebase.database().ref("/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/offtime").set(offTime);
			}

			function updateSchedulerValues(portIndex) {
				var daysPath = "/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/days";
				var onTimePath = "/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/ontime";
				var offTimePath = "/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/offtime";

				firebase.database().ref(daysPath).once("value", function(snapshot) {
					var daysString = snapshot.val();
					var days = [];

					if (daysString) {
						days = JSON.parse(daysString);
					}

					// Update the checkboxes based on the retrieved days
					$("#port" + portIndex + " .scheduler input[type='checkbox']").each(function() {
						var day = $(this).next().text();
						$(this).prop("checked", days.includes(day));
					});

					// Apply the CSS class for the selected days
					$("#port" + portIndex + " .scheduler input[type='checkbox']:checked + label").addClass("active");
				});

				firebase.database().ref(onTimePath).once("value", function(snapshot) {
					var onTime = snapshot.val();
					$("#port" + portIndex + "-on-time").val(onTime);
				});

				firebase.database().ref(offTimePath).once("value", function(snapshot) {
					var offTime = snapshot.val();
					$("#port" + portIndex + "-off-time").val(offTime);
				});
			}

			// Listen for changes in the database and update UI accordingly
			var daysPath = "/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/AutomaticOnOff/port" + portIndex + "/days";
			var firebaseRefDays = database.ref(daysPath);

			firebaseRefDays.on("value", function(snapshot) {
				var daysString = snapshot.val();
				var days = [];

				if (daysString) {
					days = JSON.parse(daysString);
				}

				// Update the checkboxes based on the retrieved days
				$("#port" + portIndex + " .scheduler input[type='checkbox']").each(function() {
					var day = $(this).next().text();
					$(this).prop("checked", days.includes(day));
				});

				// Apply the CSS class for the selected days
				$("#port" + portIndex + " .scheduler input[type='checkbox']:checked + label").addClass("active");
			});

		})(i);
	}

	
    function runSpeechRecognition() {
        var output = document.getElementById("output");
        var action = document.getElementById("action");
        var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition;
        var recognition = new SpeechRecognition();
    
        recognition.onstart = function() {
            action.innerHTML = "<small>listening, please speak...</small>";
        };
    
        recognition.onspeechend = function() {
            action.innerHTML = "<small>stopped listening, hope you are done...</small>";
            recognition.stop();
        }
    
        recognition.onresult = function(event) {
            var transcript = event.results[0][0].transcript;
            var confidence = event.results[0][0].confidence;
            output.innerHTML = "<b>Text:</b> " + transcript + "<br/> <b>Confidence:</b> " + confidence*100+"%";
            output.classList.remove("hide");
            
            // Check for specific commands and perform actions
            if (transcript.includes('turn on one')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port1').set('1');
                console.log("Turning on port one...");
            } else if (transcript.includes('turn off one')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port1').set('0');
                console.log("Turning off port one...");
            }
			if (transcript.includes('turn on two')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port2').set('1');
                console.log("Turning on port two...");
            } else if (transcript.includes('turn off two')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port2').set('0');
                console.log("Turning off port two...");
            }
            
			if (transcript.includes('turn on three')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port3').set('1');
                console.log("Turning on port three...");
            } else if (transcript.includes('turn off three')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port3').set('0');
                console.log("Turning off port three...");
            }
            
			if (transcript.includes('turn on four')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port4').set('1');
                console.log("Turning on port four...");
            } else if (transcript.includes('turn off four')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port4').set('0');
                console.log("Turning off port four...");
            }
            
			if (transcript.includes('turn on five')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port5').set('1');
                console.log("Turning on port five...");
            } else if (transcript.includes('turn off five')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port5').set('0');
                console.log("Turning off port five...");
            }

			if (transcript.includes('turn on six')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port6').set('1');
                console.log("Turning on port six...");
            } else if (transcript.includes('turn off six')) {
                firebase.database().ref('/users/byMnLOU0OZPemFHbvsPj3cP8x3l2/components/port6').set('0');
                console.log("Turning off port six...");
            }
            
    
        };
    
        recognition.start();
    }

    // Attach the runSpeechRecognition function to the button click event
    $("#speechButton").click(runSpeechRecognition);

});
  
  


  
  // User login form
const loginForm = document.querySelector("#login-form");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const errorMessage = document.getElementById("error-message");
const pageContainer = document.getElementById("page-container");
const logoutButton = document.getElementById("logout-btn");
const loginButton = document.getElementById("login-button");
const hiddenSubmitButton = document.getElementById("hidden-submit");
  
  // Predefined email and password for login
  const predefinedEmail = "asda87004@gmail.com";
  const predefinedPassword = "123456";
  
  loginButton.addEventListener("click", (e) => {
	e.preventDefault();
  
	const email = emailInput.value;
	const password = passwordInput.value;
  
	if (email === predefinedEmail && password === predefinedPassword) {
	  // Sign in with email and password
	  firebase.auth().signInWithEmailAndPassword(email, password)
		.then(() => {
		  // User login successful
		  console.log("User logged in:", email);
		  showPage();
		})
		.catch((error) => {
		  // User login failed
		  console.log("Login error:", error);
		  errorMessage.textContent = error.message;
		});
	} else {
	  // Invalid email or password
	  errorMessage.textContent = "Invalid email or password";
	}
  });
  
  logoutButton.addEventListener("click", () => {
	firebase.auth().signOut()
	  .then(() => {
		// User logout successful
		console.log("User logged out");
		showLoginForm();
	  })
	  .catch((error) => {
		// Logout error
		console.log("Logout error:", error);
	  });
  });
  
  firebase.auth().onAuthStateChanged((user) => {
	if (user) {
	  // User is signed in, display the main page
	  showPage();
	} else {
	  // User is not signed in, display the login form
	  showLoginForm();
	}
  });
  
  function showLoginForm() {
	loginForm.style.display = "block";
	pageContainer.style.display = "none";
  }
  


  function showPage() {
	loginForm.style.display = "none";
	pageContainer.style.display = "block";
	document.getElementById("login-container").style.display = "none";
  }
  


  
  
 
  // 
  