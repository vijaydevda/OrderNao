var userNameAvaliable=0;

$(document).ready(function() {

	
	$("#nextButton").click(function() {
		var clientPhoneNumber = $("#clientphone").val();
		console.log("client phonenumber "+clientPhoneNumber);
		var phoneNumberLength=clientPhoneNumber.length;
		if(phoneNumberLength <10 || phoneNumberLength>10){
			alert("Phonenumber should contain 10 digits");
		}else{
			alert("Correct Phone number");
			$.ajax({
				url : '/saveContactNumber',
				type : 'POST',
				data : {
					'contactNumber' : clientPhoneNumber
				},
				success : function(result) {
					if(result=="incorrectPhoneNumber"){
						alert("Phonenumber should contain 10 digits");
						location.href = "homepage.html"
					}else if(result=="success"){
						location.href = "summary.html";						
					}else if(result=="error"){
						alert("Some error occured, please try again");
						location.href = "homepage.html";
					}
				},
				error : function() {
					alert("Some error occured, please try again");
					location.href = "homepage.html"
				}
			});	
		}
	});

	
	
	$("#cancelorder").click(function(){
		$("#commentBox").toggle()
		if (!$("#commentBox").css('visibility') === 'hidden') {
			alert("visible");
		}
	});
	
	$("#addEmployee").click(function(){
		console.log("addEmployee");
		var username=$("#username").val();
		var firstname=$("#firstname").val();
		var lastname=$("#lastname").val();
		var password=$("#password").val();
		var email=$("#email").val();
		var confirmPassword=$("#confirmPassword").val();
		var employeeType=$("#employeeType").val();
		console.log("Employee type :- "+employeeType);username
		if(username.length==0){
			alert("User Name can't be empty");
		}else if(firstname.length==0){
			alert("First Name can't be empty");
		}else if(password.length==0){
			alert("Password  can't be empty");
		}else if(confirmPassword.length==0){
			alert("Confirm password can't be empty");
		}else if (password != confirmPassword) {
			alert("Passwords don't match, Try again");
		}else{
			$.ajax({
				url:"/addEmployee",
				method:"POST",
				data:{
					"userName":username,
					"firstName":firstname,
					"lastName":lastname,
					"password":password,
					"confirmPassword":confirmPassword,
					"email":email,
					"employeeType":employeeType
				},
				success:function(result){
					
					console.log("Result :- "+result);
					if(result=="emptyUsername"){
						alert("Username can't be empty");
					}else if(result=="userNameNotAvailable"){
						alert("Username exist, try another");
					}else if(result=="emptyFirstname"){
						alert("First Name can't be empty");
					}else if(result=="emptyPassword"){
						alert("Password can't be empty");
					}else if(result=="emptyConfirmPassword"){
						alert("Confirm Password  can't be empty");
					}else if (result=="passwordsDontMatch") {
						alert("Passwords don't match, try again");
					}else if(result=="fail"){
						alert("Some error occured, please try again");
					}else if(result=="success"){
						alert("Employee added successfully...");
					}
				},
				error : function(xhr,textStatus,error) {
					console.log("Error occured "+error);
					location.href = "addemploy.html";
				}
			});
		}
	});
	
	
});



function placeOrder(){
	console.log("placeorder");
	var newCustomer=$("#newCustomer").val();
	console.log("New Customer :- "+newCustomer);
	if(newCustomer=="TRUE"){
		var comments=$("#commentContent").val();
		var customerFirstName = $("#customerFirstName").val();
		var customerLastName = $("#customerLastName").val();
		var customerAddress = $("#customerAddress").val();
		var itemName = $("#itemName").val();
		var orderPickedFrom = $("#origin").val();
		var orderDeliveredAt = $("#destination").val();
		var totalDistance = parseInt(km);
		var serviceCharge = parseFloat(amount);
		console.log(orderPickedFrom+" "+orderDeliveredAt+" "+totalDistance+" "+serviceCharge);
		console.log("comments :- "+comments);
		if(customerFirstName.length==0){
			alert("First Name can't be empty");
		}else
		if(itemName.length==0){
			alert("Item Name can't be empty");
		}else
		if(orderPickedFrom.length==0){
			alert("Picking address can't be empty");
		}else
		if(orderDeliveredAt.length==0){
			alert("Delivery address can't be empty");
		}else{
			$.ajax({
				url : '/saveNewCustomerAndOrder',
				type : 'POST',
				data : {
					"comments":comments,
					"customerFirstName" : customerFirstName,
					"customerLastName" : customerLastName,
					"customerAddress" : customerAddress,
					"itemName" : itemName,
					"orderPickedFrom" : orderPickedFrom,
					"orderDeliveredAt" : orderDeliveredAt,
					"totalDistance" : totalDistance,
					"serviceCharge" : serviceCharge
				},
				success : function(result) {
					console.log(result);
					if(result=="emptyFirstName"){
						alert("FirstName can't be empty");
					}else if(result=="emptyItem"){
						alert("Item can't be empty");
					}else if(result=="emptyPickupLocation"){
						alert("Pickup location can't be empty");
					}else if(result=="emptyDeliveryLocation"){
						alert("Delivery location can't be empty");
					}else if(result=="incorrectDistance"){
						alert("Distance can't be 0");
					}else if(result=="incorrectServiceCharge"){
						alert("Service Charge can't be 0");
					}else if (result == "success") {
						console.log("success");
						location.href = "trackDelivery.html";
					} else if (result == "fail") {
						console.log("fail");
						location.href = "summary.html"
					}
				},
				error : function(xhr,textStatus,error) {
					console.log("Error occured "+error);
					location.href = "summary.html";
				}
			});
		}
		
	}else if(newCustomer=="FALSE"){
		var comments=$("#commentContent").val();
		var itemName = $("#itemName").val();
		var orderPickedFrom = $("#origin").val();
		var orderDeliveredAt = $("#destination").val();
		var totalDistance = parseInt(km);
		var serviceCharge = parseFloat(amount);
		console.log(itemName+" "+orderPickedFrom+"  : "+orderDeliveredAt+" "+totalDistance+" "+serviceCharge);
		console.log("comments :- "+comments);
		if(itemName.length==0){
			alert("Item Name can't be empty");
		}else
		if(orderPickedFrom.length==0){
			alert("Picking address can't be empty");
		}else
		if(orderDeliveredAt.length==0){
			alert("Delivery address can't be empty");
		}
		else{
				$.ajax({
					url : '/saveNewOrderForExistingCustomer',
					type : 'POST',
					data : {
						"comments":comments,
						"itemName" : itemName,
						"orderPickedFrom" : orderPickedFrom,
						"orderDeliveredAt" : orderDeliveredAt,
						"totalDistance" : totalDistance,
						"serviceCharge" : serviceCharge
					},
					success : function(result) {
						console.log(result);
						if(result=="emptyItem"){
							alert("Item can't be empty");
						}else if(result=="emptyPickupLocation"){
							alert("Pickup location can't be empty");
						}else if(result=="emptyDeliveryLocation"){
							alert("Delivery location can't be empty");
						}else if(result=="incorrectDistance"){
							alert("Distance can't be 0");
						}else if(result=="incorrectServiceCharge"){
							alert("Service Charge can't be 0");
						}else if (result == "success") {
							console.log("success");
							location.href = "trackDelivery.html";
						} else if (result == "fail") {
							console.log("fail");
							location.href = "summary.html"
						}
					},
					error : function(xhr,textStatus,error) {
						console.log("Error occured "+error);
						location.href = "summary.html"
					}
				});
			}
	}
}
