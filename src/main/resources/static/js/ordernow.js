var addMoreButtonCounter = 1;

// these two variables are used to reset value of dropdown when operation is
// cancelled
var currentElementId = -1;
var oldStatus;

$(document)
		.ready(
				function() {

					$("#nextButton")
							.click(
									function() {
										var clientPhoneNumber = $(
												"#clientphone").val();
										console.log("client phonenumber "
												+ clientPhoneNumber);
										var phoneNumberLength = clientPhoneNumber.length;
										if (phoneNumberLength < 10
												|| phoneNumberLength > 10) {
											alert("Phonenumber should contain 10 digits");
										} else {
											// alert("Correct Phone number");
											$
													.ajax({
														url : '/save-contact-number',
														type : 'POST',
														data : {
															'contactNumber' : clientPhoneNumber
														},
														success : function(
																result) {
															if (result == "incorrectPhoneNumber") {
																alert("Phonenumber should contain 10 digits");
																location.href = "homepage.html"
															} else if (result == "success") {
																location.href = "summary.html";
															} else if (result == "error") {
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

					$("#cancelorder").click(function() {
						$("#commentBox").toggle()
						if (!$("#commentBox").css('visibility') === 'hidden') {
							// alert("visible");
						}
					});
					// this function is used to add employee
					$("#addEmployee")
							.click(
									function() {
										console.log("addEmployee");
										var username = $("#username").val();
										var firstname = $("#firstname").val();
										var lastname = $("#lastname").val();
										var password = $("#password").val();
										var email = $("#email").val();
										var confirmPassword = $(
												"#confirmPassword").val();
										var employeeType = $("#employeeType")
												.val();
										var mobileNumber = $("#contactNumber")
												.val();
										console.log("FirstName " + firstname
												+ " LastName " + lastname
												+ " UserName " + username
												+ " Password " + password
												+ " Email " + email
												+ " ConfirmPassword "
												+ confirmPassword
												+ " MobileNumber "
												+ mobileNumber
												+ " Employee type :- "
												+ employeeType);

										if (username.length == 0) {
											alert("User Name can't be empty");
										} else if (firstname.length == 0) {
											alert("First Name can't be empty");
										} else if (lastname.length == 0) {
											alert("Last Name can't be empty");
										} else if (mobileNumber.length == 0
												|| mobileNumber.length < 0
												|| mobileNumber.length > 10) {
											alert("Please enter valid mobile number");
										} else if (password.length == 0) {
											alert("Password  can't be empty");
										} else if (confirmPassword.length == 0) {
											alert("Confirm password can't be empty");
										} else if (password != confirmPassword) {
											alert("Passwords don't match, Try again");
										} else {
											$
													.ajax({
														url : "/addEmployee",
														method : "POST",
														data : {
															"userName" : username,
															"firstName" : firstname,
															"lastName" : lastname,
															"password" : password,
															"confirmPassword" : confirmPassword,
															"email" : email,
															"employeeType" : employeeType,
															"contactNumber" : mobileNumber
														},
														success : function(
																result) {

															console
																	.log("Result :- "
																			+ result);
															if (result == "emptyUsername") {
																alert("Username can't be empty");
															} else if (result == "invalidContactNumber") {
																alert("Please enter a phone number");
															} else if (result == "userNameNotAvailable") {
																alert("Username exist, try another");
															} else if (result == "emptyFirstname") {
																alert("First Name can't be empty");
															} else if (result == "emptyLastName") {
																alert("Last Name can't be empty");
															} else if (result == "emptyPassword") {
																alert("Password can't be empty");
															} else if (result == "emptyConfirmPassword") {
																alert("Confirm Password  can't be empty");
															} else if (result == "passwordsDontMatch") {
																alert("Passwords don't match, try again");
															} else if (result == "fail") {
																alert("Some error occured, please try again");
															} else if (result == "success") {
																alert("Employee added successfully...");
															}
														},
														error : function(xhr,
																textStatus,
																error) {
															console
																	.log("Error occured "
																			+ error);
															location.href = "addemploy.html";
														}
													});
										}
									});

					$("#addMore")
							.click(
									function() {
										console.log("AddMore:- ");
										console.log("counter :- "
												+ addMoreButtonCounter);
										if (addMoreButtonCounter < 5) {
											addMoreButtonCounter++;
											console.log("counter :- "
													+ addMoreButtonCounter);
											$("#removeButton123").remove();
											var label = $("<label>")
													.attr(
															{
																'id' : "originLabel"
																		+ addMoreButtonCounter
															})
													.text(
															'Pick Up Address:'
																	+ addMoreButtonCounter);
											var input = $('<input type="text">')
													.attr(
															{
																id : 'origin'
																		+ addMoreButtonCounter,
																placeholder : "order pick up address"
																		+ addMoreButtonCounter
															});
											var label2 = $("<label>")
													.attr(
															{
																'id' : "destinationLabel"
																		+ addMoreButtonCounter
															})
													.text(
															'Delivery Address:'
																	+ addMoreButtonCounter);
											var input2 = $(
													'<input type="text">')
													.attr(
															{
																id : 'destination'
																		+ addMoreButtonCounter,
																placeholder : "order delivery address"
																		+ addMoreButtonCounter
															});
											var label3 = $("<label>")
													.attr(
															{
																'id' : "itemLabel"
																		+ addMoreButtonCounter
															})
													.text(
															'Order :'
																	+ addMoreButtonCounter);
											var input3 = $(
													'<input type="text">')
													.attr(
															{
																id : 'itemName'
																		+ addMoreButtonCounter,
																placeholder : "order "
																		+ addMoreButtonCounter
																		+ " name"
															});
											var button = $("<button>").attr({
												'type' : "button",
												'id' : 'removeButton123'
											}).text('Remove');
											input.appendTo(label);
											input2.appendTo(label2);
											input3.appendTo(label3);
											button.appendTo(label3);
											$('#locationDiv').append(label);
											$('#locationDiv').append(label2);
											$('#locationDiv').append(label3);
										}
									});

					$('#locationDiv').on(
							'click',
							'#removeButton123',
							function() {
								console.log("RemoveButton :-");
								console.log("counter :- "
										+ addMoreButtonCounter);
								if (addMoreButtonCounter < 6) {
									$("#originLabel" + addMoreButtonCounter)
											.remove();
									$(
											"#destinationLabel"
													+ addMoreButtonCounter)
											.remove();
									$("#itemLabel" + addMoreButtonCounter)
											.remove();
									$("#origin" + addMoreButtonCounter)
											.remove();
									$("#destination" + addMoreButtonCounter)
											.remove();
									$("#itemName" + addMoreButtonCounter)
											.remove();
									$(this).remove();
									addMoreButtonCounter--;
									if (addMoreButtonCounter > 1) {
										var button = $("<button>").attr({
											'type' : "button",
											'id' : 'removeButton123'
										}).text('Remove');
										$('#locationDiv').append(button);
									}
									console.log("counter :- "
											+ addMoreButtonCounter);
								}
							});
					// This function is called when status filter is changed and
					// it will fetch data of corrosponding status from DB
					$("#byStatus")
							.change(
									function() {
										var status = $(this).val();
										console.log("status " + status);
										/*
										 * //This is used to check whether value
										 * of selected option(status) is zero if
										 * it will be zero then we display
										 * select correct status else we forward
										 * request
										 * 
										 * if (status.length == 0) {
										 * alert("Please select the correct
										 * status"); } else {
										 */
										try {
											$("#trackOrderTable")
													.load(
															"/filter-orders-by-status?status="
																	+ status,
															function() {
																// This modal
																// will be shown
																// if
																// any error or
																// suspicious
																// activity is
																// found
																$(
																		'#SuspiciousActivityModal')
																		.modal(
																				{
																					backdrop : 'static',
																					keyboard : true,
																					show : true
																				});
															});
										} catch (err) {
											var r
											console.log(err);
										}

										// }
									});
					// This function is used to filter track order by
					// time(today,last 7 days,this month,till date)
					$("#byTime").change(
							function() {
								var date = $(this).val();
								console.log("date " + date);
								$("#trackOrderTable").load(
										"/filter-orders-by-date?date=" + date,
										function() {
											// This modal will be shown if
											// any error or suspicious
											// activity is found
											$('#SuspiciousActivityModal')
													.modal({
														backdrop : 'static',
														keyboard : true,
														show : true
													});
										});
							});
					// This function is used to filter track order by
					// assignment(assigned or not assigned(deliveryBoys))
					$("#byAssignment").change(
							function() {
								var assignment = $(this).val();
								console.log("assignment " + assignment);
								$("#trackOrderTable").load(
										"/filter-orders-by-assignment?assignment="
												+ assignment,
										function() {
											// This modal will be shown if
											// any error or suspicious
											// activity is found
											$('#SuspiciousActivityModal')
													.modal({
														backdrop : 'static',
														keyboard : true,
														show : true
													});
										});
							});

					$("#trackDeliverySearchButton")
							.click(
									function() {
										console.log("Search Button Called...");
										var searchKey = $(
												"#trackDeliverySearchKey")
												.val();
										var trimmedSearchKey = searchKey.trim();
										if (trimmedSearchKey.length == 0) {
											alert("Enter search key to search something...");
										} else {
											console.log("searchKey "
													+ searchKey);
											$("#trackOrderTable").load(
													"/track-delivery-search?searchKey="
															+ searchKey,
													function() {
														// alert("Search
														// Success...");
													});
										}
									});

				});

function pendingOrDeliveredOrderConfirmation() {
	$("#trackOrderTable").load("/save-pending-or-delivered-status-of-order",
			function() {
				$('#SuspiciousActivityModal').modal({
					backdrop : 'static',
					keyboard : true,
					show : true
				});
			});
}

function failedOrderConfirmation() {
	var failedComments = $("#failedStatusComments").val();
	console.log("Failed Status Comments :- " + failedComments);
	if (!failedComments && failedComments.trim().length == 0) {
		alert("Failed status comments can't be empty.");
	} else {
		$("#trackOrderTable").load("/save-failed-status-comments-of-order", {
			"failedComments" : failedComments
		}, function() {
			$('#SuspiciousActivityModal').modal({
				backdrop : 'static',
				keyboard : true,
				show : true
			});
		});
	}
}

// This function is called when status confirmation modal is closed
function setDefaultStatus() {
	console.log("Set Default Status : OldStatus :- " + oldStatus
			+ " currentElementId :- " + currentElementId);
	var myexp = '#' + currentElementId + ' option[value="' + oldStatus + '"]';
	console.log(myexp);
	$(myexp).prop('selected', true);
}

// This function is called when current status is changed from current status
// dropdown
function currentOrderStatusFunc(orderNumber, currentStatus, previousStatus) {
	var status = $(currentStatus).val();
	currentElementId = $(currentStatus).attr('id');
	oldStatus = previousStatus;
	console.log("Pending Status OrderNumber :- " + orderNumber + "  Status :- "
			+ status + " currentElementId :- " + currentElementId+" Previous Status :- "+oldStatus);
	// Here we check status value for null & length
	// if current status value is null or empty the a suspicious activity alert
	// is shown

	if (status && status.trim().length > 0) {
		if (status == "Failed") {
			// if status is failed the we show modal asking for failed
			// comments(reason why order is failed)
			$.ajax({
				url : "/order-status-modal?orderNumber=" + orderNumber
						+ "&newOrderStatus=" + status,
				method : "GET",
				success : function(data) {
					if (data == "success") {
						$("#failedStatusConfirmModal").modal({
							backdrop : 'static',
							keyboard : true,
							show : true
						});
					}
				},
				error : function(xhr) {
					Console.log('Request Status: ' + xhr.status
							+ ' Status Text: ' + xhr.statusText + ' '
							+ xhr.responseText);
				}
			});

		} else if (status == "Pending" || status == "Delivered") {
			$.ajax({
				url : "/order-status-modal?orderNumber=" + orderNumber
						+ "&newOrderStatus=" + status,
				method : "GET",
				success : function(data) {
					if (data == "success") {
						$("#pendingOrDeliveredConfirmModal").modal({
							backdrop : 'static',
							keyboard : true,
							show : true
						});
					}
				},
				error : function(xhr) {
					Console.log('Request Status: ' + xhr.status
							+ ' Status Text: ' + xhr.statusText + ' '
							+ xhr.responseText);
				}
			});
		}
	} else {
		alert("Suspicious Activity");
	}

}

function failedOrderStatus(orderNumber) {
	console.log("Order Number :- " + orderNumber);
	$("#failedOrderDetailsModal").load(
			"/comments-of-failed-order?orderNumber=" + orderNumber,
			function(response, status, xhr) {
				if (status == "success") {
					$("#failedOrderModal").modal({
						backdrop : 'static',
						keyboard : true,
						show : true
					});
				}
			});
}

function assignDeliveryBoyFunc(orderNumber, deliveryBoyId) {
	var deliveryBoyId = $(deliveryBoyId).val();
	console.log("assignDeliveryBoyFunc() Order Number :- " + orderNumber
			+ " Delivery Body Id :- " + deliveryBoyId);
	$("#trackOrderTable").load("/assign-order-to-delivery-boy", {
		"orderNumber" : orderNumber,
		"deliveryBoyId" : deliveryBoyId
	}, function(response, status, xhr) {
		if (status == "success") {
			$("#suspiciousActivity").modal({
				backdrop : 'static',
				keyboard : false,
				show : true
			});
		}
	});

}

/*
 * function deliveryBoyAssignment(orderNumber) { var deliveryBoyId =
 * $("#assignDeliveryBoy").val(); alert("delivery boy id :- " +
 * $("#assignDeliveryBoy").val() + " OrderNumber :- " + orderNumber); $.ajax({
 * url : "/assign-order-to-delivery-boy", method : "POST", data : {
 * "deliveryBoyId" : deliveryBoyId, "orderNumber" : orderNumber }, success :
 * function(data) { if (data == "success") { alert("Delivery Boy successfully
 * assigned."); } else if (data == "fail") { alert("Delivery Boy assignment
 * failed."); } }, error : function() { alert("some error occured, try again") }
 * }); }
 */

function moreDetails(orderNumber) {
	// this function is used to fetch more details of corrosponding order(for
	// multiple orders)
	console.log("More details for order number :- " + orderNumber);
	$("#moreDetailModal").load(
			"/more-detail-of-order?orderNumber=" + orderNumber, function() {
				$("#orderDetailsModal").modal({
					backdrop : 'static',
					keyboard : true,
					show : true
				});
			});
}

function placeOrder() {
	console.log("placeorder");
	var newCustomer = $("#newCustomer").val();
	console.log("New Customer :- " + newCustomer);
	if (newCustomer == "TRUE") {
		var comments = $("#commentContent").val();
		var customerFirstName = $("#customerFirstName").val();
		var customerLastName = $("#customerLastName").val();
		var customerAddress = $("#customerAddress").val();
		var itemName = [];
		itemName[0] = $("#itemName").val() + "&#@";
		var orderPickedFrom = [];
		orderPickedFrom[0] = $("#origin").val() + "&#@";
		var orderDeliveredAt = [];
		orderDeliveredAt[0] = $("#destination").val() + "&#@";
		console.log("itemName[0] " + itemName[0] + " orderPickedFrom [0] "
				+ orderPickedFrom[0] + " orderDeliveredAt [0] "
				+ orderDeliveredAt[0]);
		var orderLength = addMoreButtonCounter;
		console.log("orderLength" + orderLength);
		if (orderLength > 1) {
			for (var i = 1; i < orderLength; i++) {
				itemName[i] = $("#itemName" + (i + 1)).val() + "&#@";
				orderPickedFrom[i] = $("#origin" + (i + 1)).val() + "&#@";
				orderDeliveredAt[i] = $("#destination" + (i + 1)).val() + "&#@";
				console.log("Value of i " + i);
				console.log("itemName[" + i + "] " + itemName[i]
						+ " orderPickedFrom [" + i + "] " + orderPickedFrom[i]
						+ " orderDeliveredAt [" + i + "] "
						+ orderDeliveredAt[i]);
			}
		}
		var totalDistance = parseInt(km);
		var serviceCharge = parseFloat(amount);
		var itemNameString = JSON.stringify(itemName);
		var orderPickedFromString = JSON.stringify(orderPickedFrom);
		var orderDeliveredAtString = JSON.stringify(orderDeliveredAt);
		console.log(orderPickedFrom.length + " " + orderDeliveredAt.length
				+ " " + totalDistance + " " + serviceCharge);
		console.log("comments :- " + comments);
		if (customerFirstName.length == 0) {
			alert("First Name can't be empty");
		} else if (itemName.length == 0) {
			alert("Item Name can't be empty");
		} else if (orderPickedFrom.length == 0) {
			alert("Picking address can't be empty");
		} else if (orderDeliveredAt.length == 0) {
			alert("Delivery address can't be empty");
		} else {
			$.ajax({
				url : '/save-new-customer-and-order',
				type : 'POST',
				data : {
					"comments" : comments,
					"customerFirstName" : customerFirstName,
					"customerLastName" : customerLastName,
					"customerAddress" : customerAddress,
					"itemName" : itemNameString,
					"orderPickedFrom" : orderPickedFromString,
					"orderDeliveredAt" : orderDeliveredAtString,
					"totalDistance" : totalDistance,
					"serviceCharge" : serviceCharge,
					"totalNumberOfOrders" : orderLength
				},
				success : function(result) {
					console.log(result);
					if (result == "emptyFirstName") {
						alert("FirstName can't be empty");
					} else if (result == "emptyItem") {
						alert("Item can't be empty");
					} else if (result == "emptyPickupLocation") {
						alert("Pickup location can't be empty");
					} else if (result == "emptyDeliveryLocation") {
						alert("Delivery location can't be empty");
					} else if (result == "incorrectDistance") {
						alert("Distance can't be 0");
					} else if (result == "incorrectServiceCharge") {
						alert("Service Charge can't be 0");
					} else if (result == "success") {
						console.log("success");
						location.href = "trackdelivery.html";
					} else if (result == "fail") {
						console.log("fail");
						location.href = "summary.html"
					}
				},
				error : function(xhr, textStatus, error) {
					console.log("Error occured " + error);
					location.href = "summary.html";
				}
			});
		}

	} else if (newCustomer == "FALSE") {
		var comments = $("#commentContent").val();
		var itemName = [];
		itemName[0] = $("#itemName").val() + "&#@";
		var orderPickedFrom = [];
		orderPickedFrom[0] = $("#origin").val() + "&#@";
		var orderDeliveredAt = [];
		orderDeliveredAt[0] = $("#destination").val() + "&#@";
		console.log("itemName[0] " + itemName[0] + " orderPickedFrom [0] "
				+ orderPickedFrom[0] + " orderDeliveredAt [0] "
				+ orderDeliveredAt[0]);
		var orderLength = addMoreButtonCounter;
		console.log("orderLength" + orderLength);
		if (orderLength > 1) {
			for (var i = 1; i < orderLength; i++) {
				itemName[i] = $("#itemName" + (i + 1)).val() + "&#@";
				orderPickedFrom[i] = $("#origin" + (i + 1)).val() + "&#@";
				orderDeliveredAt[i] = $("#destination" + (i + 1)).val() + "&#@";
				console.log("Value of i " + i);
				console.log("itemName[" + i + "] " + itemName[i]
						+ " orderPickedFrom [" + i + "] " + orderPickedFrom[i]
						+ " orderDeliveredAt [" + i + "] "
						+ orderDeliveredAt[i]);
			}
		}
		var totalDistance = parseInt(km);
		var serviceCharge = parseFloat(amount);
		var itemNameString = JSON.stringify(itemName);
		var orderPickedFromString = JSON.stringify(orderPickedFrom);
		var orderDeliveredAtString = JSON.stringify(orderDeliveredAt);
		console.log(itemName.length + " " + orderPickedFrom.length + "  : "
				+ orderDeliveredAt.length + " " + totalDistance + " "
				+ serviceCharge);
		console.log("comments :- " + comments);
		if (itemName.length == 0) {
			alert("Item Name can't be empty");
		} else if (orderPickedFrom.length == 0) {
			alert("Picking address can't be empty");
		} else if (orderDeliveredAt.length == 0) {
			alert("Delivery address can't be empty");
		} else {
			$.ajax({
				url : '/saveNewOrderForExistingCustomer',
				type : 'POST',
				data : {
					"comments" : comments,
					"itemName" : itemNameString,
					"orderPickedFrom" : orderPickedFromString,
					"orderDeliveredAt" : orderDeliveredAtString,
					"totalDistance" : totalDistance,
					"serviceCharge" : serviceCharge,
					"totalNumberOfOrders" : orderLength
				},
				success : function(result) {
					console.log(result);
					if (result == "emptyItem") {
						alert("Item can't be empty");
					} else if (result == "emptyPickupLocation") {
						alert("Pickup location can't be empty");
					} else if (result == "emptyDeliveryLocation") {
						alert("Delivery location can't be empty");
					} else if (result == "incorrectDistance") {
						alert("Distance can't be 0");
					} else if (result == "incorrectServiceCharge") {
						alert("Service Charge can't be 0");
					} else if (result == "success") {
						console.log("success");
						location.href = "trackdelivery.html";
					} else if (result == "fail") {
						console.log("fail");
						location.href = "summary.html"
					}
				},
				error : function(xhr, textStatus, error) {
					console.log("Error occured " + error);
					location.href = "summary.html"
				}
			});
		}
	}
}
