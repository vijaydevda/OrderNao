package com.ordernao.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ordernao.bean.EmployeeBean;
import com.ordernao.bean.OrderBean;
import com.ordernao.service.OrderService;
import com.ordernao.utility.OrderNaoConstants;

@Controller
public class OrderController {
	static final Logger logger = LogManager.getLogger(OrderController.class.getName());
	@Autowired
	OrderService service;

	/*
	 * @RequestMapping(value = Constants.PATH_TRACK_DELIVERY_TABLE_HTML_PAGE)
	 * public String TrackDeliveryDetails(HttpServletRequest request,
	 * HttpServletResponse response) { logger.info("TrackDeliveryDetails");
	 * return Constants.PATH_TRACK_DELIVERY_TABLE_PAGE; }
	 * 
	 * @RequestMapping(value = "trackDeliveryTable") public String
	 * TrackDeliveryTable(HttpServletRequest request, HttpServletResponse
	 * response) { logger.info("TrackDeliveryDetails"); return
	 * "fragments/trackDeliveryTable"; }
	 */

	@RequestMapping(value = OrderNaoConstants.PATH_MORE_ORDER_DETAILS_HTML_PAGE)
	public String moreDetails(HttpServletRequest request, HttpServletResponse response) {
		return OrderNaoConstants.PATH_MORE_ORDER_DETAILS_PAGE;
	}

	@RequestMapping(value = OrderNaoConstants.PATH_SUMMARY_HTML_PAGE)
	public String showHome(HttpServletRequest request, HttpServletResponse response) {
		return OrderNaoConstants.PATH_SUMMARY_PAGE;
	}

	@RequestMapping(value = OrderNaoConstants.PATH_TRACK_DELIVERY_HTML_PAGE)
	public String trackDelivery(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("Entry at trackDelivery(Controller)");
		List<OrderBean> orderBeanList = service.getOrderDetailsOfCustomer();
		List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
		model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, orderBeanList);
		model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
		logger.info("Exit at trackDelivery(Controller)");
		return OrderNaoConstants.PATH_TRACK_DELIVERY_PAGE;
	}

	@RequestMapping(value = OrderNaoConstants.PATH_SAVE_CONTACT_NUMBER, method = RequestMethod.POST)
	@ResponseBody
	public String saveContactNumber(@RequestParam(OrderNaoConstants.REQUEST_PARAM_CONTACT_NUMBER) String contactNumber,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		logger.info("Entry at SaveContactNumberMethod(Controller)");
		logger.debug("Contact Number :- " + contactNumber);
		if (contactNumber.isEmpty() || contactNumber.length() < OrderNaoConstants.MAX_MOBILE_NUMBER_LENGTH
				|| contactNumber.length() > OrderNaoConstants.MAX_MOBILE_NUMBER_LENGTH) {
			return OrderNaoConstants.RETURN_STATUS_INCORRECT_MOBILE_NUMBER;
		} else {
			session.invalidate();
			session = request.getSession();
			session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_CONTACT_NUMBER, contactNumber);
			OrderBean orderBean = service.checkExistingCustomer(contactNumber);
			String firstName = orderBean.getCustomerFirstName();
			logger.info("OrderBean :- " + orderBean.getCustomerFirstName());
			/* if (firstName != null) { */
			session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_ORDERBEAN, orderBean);
			boolean newCustomer = orderBean.isNewCustomer();
			if (newCustomer) {
				session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_NEW_CUSTOMER, OrderNaoConstants.STRING_TRUE);
			} else {
				session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_NEW_CUSTOMER, OrderNaoConstants.STRING_FALSE);
			}
			return OrderNaoConstants.RETURN_STATUS_SUCCESS;
			/*
			 * } else { return Constants.RETURN_STATUS_ERROR; }
			 */

		}

	}

	@RequestMapping(OrderNaoConstants.PATH_SAVE_NEW_CUSTOMER)
	@ResponseBody
	public String saveNewCustomer(@ModelAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ORDERBEAN) OrderBean orderBean,
			HttpSession session) {
		logger.info("Entry at saveNewCustomer(Controller) ");
		logger.info("Order Length :- " + orderBean.getTotalNumberOfOrders());
		String contactNumber = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_CONTACT_NUMBER);
		String customerFirstName = orderBean.getCustomerFirstName();
		String itemName = (String) orderBean.getItemName();
		String orderPickedFrom = (String) orderBean.getOrderPickedFrom();
		String orderDeliveredAt = (String) orderBean.getOrderDeliveredAt();
		int totalDistance = orderBean.getTotalDistance();
		double serviceCharge = orderBean.getServiceCharge();
		if (customerFirstName == null || customerFirstName.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_FIRSTNAME;
		} else if (itemName == null || itemName.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_ITEM;
		} else if (orderPickedFrom == null || orderPickedFrom.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_PICKUP_LOCATION;
		} else if (orderDeliveredAt == null || orderDeliveredAt.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_DELIVERY_LOCATION;
		} else if (totalDistance < 0) {
			return OrderNaoConstants.RETURN_STATUS_INCORRECT_DISTANCE;
		} else if (serviceCharge < 0) {
			return OrderNaoConstants.RETURN_STATUS_INCORRECT_SERVICE_CHARGE;
		}
		orderBean.setCustomerPhone(contactNumber);

		try {
			boolean status = service.saveNewCustomer(orderBean);
			if (status) {
				return OrderNaoConstants.RETURN_STATUS_SUCCESS;
			} else {
				return OrderNaoConstants.RETURN_STATUS_FAIL;
			}
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return OrderNaoConstants.RETURN_STATUS_FAIL;
		}

	}

	@RequestMapping(OrderNaoConstants.PATH_SAVE_NEW_ORDER_FOR_EXISTING_CUSTOMER)
	@ResponseBody
	public String saveNewOrderForExistingCustomer(
			@ModelAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ORDERBEAN) OrderBean orderBean, HttpSession session) {
		logger.info("Entry at saveNewOrderForExistingCustomer(Controller) ");
		logger.info("Order Length :- " + orderBean.getTotalNumberOfOrders());
		String contactNumber = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_CONTACT_NUMBER);
		orderBean.setCustomerPhone(contactNumber);
		String itemName = (String) orderBean.getItemName();
		String orderPickedFrom = (String) orderBean.getOrderPickedFrom();
		String orderDeliveredAt = (String) orderBean.getOrderDeliveredAt();
		int totalDistance = orderBean.getTotalDistance();
		double serviceCharge = orderBean.getServiceCharge();
		if (itemName == null || itemName.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_ITEM;
		} else if (orderPickedFrom == null || orderPickedFrom.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_PICKUP_LOCATION;
		} else if (orderDeliveredAt == null || orderDeliveredAt.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_DELIVERY_LOCATION;
		} else if (totalDistance < 0) {
			return OrderNaoConstants.RETURN_STATUS_INCORRECT_DISTANCE;
		} else if (serviceCharge < 0) {
			return OrderNaoConstants.RETURN_STATUS_INCORRECT_SERVICE_CHARGE;
		}

		try {
			boolean status = service.insertOrderDetails(orderBean);
			if (status) {
				logger.info("Exit at saveNewOrderForExistingCustomer(Controller) ");
				return OrderNaoConstants.RETURN_STATUS_SUCCESS;
			} else {
				logger.info("Exit at saveNewOrderForExistingCustomer(Controller) ");
				return OrderNaoConstants.RETURN_STATUS_FAIL;
			}
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return OrderNaoConstants.RETURN_STATUS_FAIL;
		}

	}

	@RequestMapping(OrderNaoConstants.PATH_ADD_EMPLOYEE)
	@ResponseBody
	public String addEmployee(@ModelAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_EMPLOYEE_BEAN) EmployeeBean employee) {
		logger.info("Entry at addEmployee(Controller) ");
		String userName = employee.getUserName();
		String firstName = employee.getFirstName();
		String lastName = employee.getLastName();
		String password = employee.getPassword();
		String confirmPassword = employee.getConfirmPassword();
		String employeeType = employee.getEmployeeType();
		String contactNumber = employee.getContactNumber();
		logger.info(userName + " " + firstName + " " + lastName + " " + password + " " + confirmPassword + " "
				+ employeeType + " " + contactNumber);
		if (userName == null || userName.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_USERNAME;
		} else if (firstName == null || firstName.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_FIRSTNAME;
		} else if (lastName == null || lastName.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_LASTNAME;
		} else if (contactNumber.length() < 0 && contactNumber.length() > OrderNaoConstants.MAX_MOBILE_NUMBER_LENGTH) {
			return OrderNaoConstants.RETURN_STATUS_INVALID_CONTACT_NUMBER;
		} else if (password == null || password.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_PASSWORD;
		} else if (confirmPassword == null || confirmPassword.isEmpty()) {
			return OrderNaoConstants.RETURN_STATUS_EMPTY_CONFIRM_PASSWORD;
		} else if (!password.equals(confirmPassword)) {
			return OrderNaoConstants.RETURN_STATUS_PASSWORD_DOESNOT_MATCH;
		}
		// Here we check whether username exist in database if exist we return
		// "username exist" else we process the request
		boolean usernameStatus = service.checkForUserName(userName);
		if (usernameStatus) {
			logger.info("Username Exists");
			logger.info("Exit at addEmployee(Controller) ");
			return OrderNaoConstants.RETURN_STATUS_USERNAME_NOT_AVAILABLE;
		}
		boolean status = service.saveEmployee(employee);
		if (status) {
			logger.info("Employee Inserted successfully ");
			logger.info("Exit at addEmployee(Controller) ");
			return OrderNaoConstants.RETURN_STATUS_SUCCESS;
		}
		logger.info("Employee Insertion failed ");
		logger.info("Exit at addEmployee(Controller) ");
		return OrderNaoConstants.RETURN_STATUS_FAIL;

	}

	// here we are fetching more details for multiple orders
	@RequestMapping(value = OrderNaoConstants.PATH_MORE_DETAILS_OF_ORDER)
	public String getMoreDetailOfOrder(@RequestParam(OrderNaoConstants.REQUEST_PARAM_ORDERNUMBER) int orderNumber,
			Model model) {
		logger.info("Entry at getMoreDetailOfOrder(Controller)");
		logger.info("Order Number :- " + orderNumber);
		List<OrderBean> moreDetailsOfOrderList = service.getMoreDetailOfOrder(orderNumber);
		model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_MORE_DETAIL_OF_ORDER, moreDetailsOfOrderList);
		logger.info("Exit at getMoreDetailOfOrder(Controller)");
		return OrderNaoConstants.RETURN_STATUS_MORE_DETAILS_OF_ORDER;
	}

	@RequestMapping(value = OrderNaoConstants.PATH_ASSIGN_ORDER_TO_DELIVERY_BOY, method = RequestMethod.POST)
	public String assignOrderToDeliveryBoy(
			@RequestParam(OrderNaoConstants.REQUEST_PARAM_DELIVERY_BOYID) int deliveryBoyId,
			@RequestParam(OrderNaoConstants.REQUEST_PARAM_ORDERNUMBER) int orderNumber, Model model) {
		logger.info("Entry at assignOrderToDeliveryBoy(Controller) Deliveryboyid :- " + deliveryBoyId);
		boolean deliveryBoyStatus = service.checkDeliveryBoyIdInDB(deliveryBoyId);
		if (deliveryBoyStatus) {
			boolean status = service.assignOrderToDeliveryBoy(deliveryBoyId, orderNumber);
			if (status) {
				List<OrderBean> orderBeanList = service.getOrderDetailsOfCustomer();
				List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, orderBeanList);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
				logger.info("Exit at assignOrderToDeliveryBoy(Controller) Deliveryboyid :- " + deliveryBoyId);
				logger.info("trackDeliveryTable(Controller)");
				return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
			} else {
				logger.info("Error(Controller)");
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
						OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
						OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
				return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
			}
		} else {
			logger.info("Suspicious Activity found.");
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
		}

	}

	// This method is used to filter track order by status
	@RequestMapping(value = OrderNaoConstants.PATH_FILTER_ORDER_BY_STATUS)
	public String filterOrdersByStatus(@RequestParam(OrderNaoConstants.REQUEST_PARAM_STATUS) String status, Model model,
			HttpSession session) {
		logger.info("Entry at filterOrdersByStatus(Controller) Status :- " + status);
		// Here we are checking for null,length & all the three
		// status(pending,delivered,failed) this is for the case if user set
		// empty value of any status
		if ((status != null && status.trim().length() > 0)
				&& (status.equalsIgnoreCase(OrderNaoConstants.ORDER_STATUS_PENDING)
						|| status.equalsIgnoreCase(OrderNaoConstants.ORDER_STATUS_DELIVERED)
						|| status.equalsIgnoreCase(OrderNaoConstants.ORDER_STATUS_FAILED))) {
			// Here we are setting status in session it will be used when user
			// filter according to time or assignment(deliveryBoys)
			session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_STATUS, status);
			List<OrderBean> filterOrderList = service.filterOrdersByStatus(status);
			List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
			logger.info("List Size :- " + filterOrderList.size());
			logger.info("Exit at filterOrdersByStatus(Controller)");
			return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
		} else {
			// if user try to change value of status(pending,delivered,failed)
			// in dropdown and send request then we display suspicious activity
			// found
			logger.info("Suspicious Activity found.");
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;

		}
	}

	// This method is used to filter track order by Date
	@RequestMapping(value = OrderNaoConstants.PATH_FILTER_ORDER_BY_DATE)
	public String filterOrdersByDate(@RequestParam(OrderNaoConstants.REQUEST_PARAM_DATE) String date, Model model,
			HttpSession session) {
		logger.info("Entry at filterOrdersByDate(Controller) Date : - " + date);
		if ((date != null && date.trim().length() > 0) && (date.equalsIgnoreCase(OrderNaoConstants.ORDER_DATE_TODAY)
				|| date.equalsIgnoreCase(OrderNaoConstants.ORDER_DATE_LAST_SEVEN_DAYS)
				|| date.equalsIgnoreCase(OrderNaoConstants.ORDER_DATE_THIS_MONTH)
				|| date.equalsIgnoreCase(OrderNaoConstants.ORDER_DATE_TILL_DATE))) {

			if (session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_STATUS) != null) {
				// if filter order by status is not null then we filter orders
				// using this priority
				// 1)By status 2)By time & we store filter ordder by time in
				// session it will be used in filter order by
				// assignment(DeliveryBoys)
				session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_TIME, date);
				String status = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_STATUS);
				List<OrderBean> filterOrderList = service.filterOrdersByStatusAndTime(date, status);
				List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
				logger.info("List Size :- " + filterOrderList.size());
				logger.info("Exit at filterOrdersByDate(Controller)");
				return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;

			} else {
				// here filter order by status is null the we filter order
				// directly by time
				List<OrderBean> filterOrderList = service.filterOrdersByDate(date);
				List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
				logger.info("List Size :- " + filterOrderList.size());
				logger.info("Exit at filterOrdersByDate(Controller)");
				return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
			}
		} else {
			logger.info("Suspicious Activity found.");
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
		}
	}

	@RequestMapping(value = OrderNaoConstants.PATH_FILTER_ORDER_BY_ASSIGNMENT)
	public String filterOrdersByAssignment(@RequestParam(OrderNaoConstants.REQUEST_PARAM_ASSIGNMENT) String assignment,
			Model model, HttpSession session) {
		logger.info("Entry at filterOrdersByAssignment(Controller) order assignment status :- " + assignment);

		// here we are checking for null and length and whether assignment value
		// contains(assigned or unassigned) or not
		// if it doesn't satisfied the value the we will return suspicious
		// activity found
		if ((assignment != null && assignment.trim().length() > 0)
				&& (assignment.equalsIgnoreCase(OrderNaoConstants.ORDER_ASSIGNED)
						|| assignment.equalsIgnoreCase(OrderNaoConstants.ORDER_UNASSIGNED))) {
			// here we filter order based on priority
			// 1)ByStatus 2)ByTime 3)ByAssignment
			if (session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_STATUS) != null) {
				// if status is not null then we fetch status from session
				String status = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_STATUS);
				if (session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_TIME) != null) {
					// if time is not null the we fetch time from session
					String time = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_TIME);
					// here we filter orders ByStatus,ByTime and ByAssignment
					List<OrderBean> filterOrderList = service.filterOrdersByStatusAndTimeAndAssignment(status, time,
							assignment);
					List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
					logger.info(" List Size :- " + filterOrderList.size());
					logger.info("Exit at filterOrdersByAssignment(Controller)");
					return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
				} else {
					// here we filter orders Only ByStatus and ByAssignment
					List<OrderBean> filterOrderList = service.filterOrdersByStatusAndAssignment(status, assignment);
					List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
					logger.info(" List Size :- " + filterOrderList.size());
					logger.info("Exit at filterOrdersByAssignment(Controller)");
					return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
				}
			} else if (session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_TIME) != null) {
				// here we filter orders by time and assigment
				String time = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_FILTER_ORDER_TIME);
				List<OrderBean> filterOrderList = service.filterOrdersByTimeAndAssignment(time, assignment);
				List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
				logger.info(" List Size :- " + filterOrderList.size());
				logger.info("Exit at filterOrdersByAssignment(Controller)");
				return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;

			} else {
				// here both time and status is null so we filter orders only by
				// assignment
				List<OrderBean> filterOrderList = service.filterOrdersByAssignment(assignment);
				List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, filterOrderList);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
				logger.info("List Size :- " + filterOrderList.size());
				logger.info("Exit at filterOrdersByAssignment(Controller)");
				return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
			}

		} else {
			logger.info("Suspicious Activity found.");
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
		}
	}

	@RequestMapping(value = OrderNaoConstants.PATH_TRACK_DELIVERY_SEARCH)
	public String searchTrackDelivery(@RequestParam(OrderNaoConstants.REQUEST_PARAM_SEARCH_KEY) String searchKey,
			Model model) {
		logger.info("Entry at searchTrackDelivery(Controller) search key:- " + searchKey);
		List<OrderBean> searchOrderList = service.searchTrackDelivery(searchKey);
		List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
		model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, searchOrderList);
		model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
		logger.info(" Search List Size :- " + searchOrderList.size());
		logger.info("Exit at searchTrackDelivery(Controller)");
		return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
	}

	@RequestMapping(value = OrderNaoConstants.PATH_COMMENTS_OF_FAILED_STATUS)
	public String getCommentsOfFailedOrder(
			@RequestParam(OrderNaoConstants.REQUEST_PARAM_ORDERNUMBER) String orderNumber, Model model) {
		logger.info("Entry at getCommentsOfFailedOrder(Controller) orderNumber:- " + orderNumber);
		boolean status = service.checkForOrderNumberAndFailedStatus(orderNumber);
		if (status) {
			OrderBean failedOrderCommentsBean = service.getCommentsOfFailedOrder(orderNumber);
			model.addAttribute("failedOrderBean", failedOrderCommentsBean);
			logger.info("Exit at getCommentsOfFailedOrder(Controller)");
			return OrderNaoConstants.PATH_FRAGMENTS_FAILED_ORDER_DETAILS;
		} else {
			logger.info("Suspicious Activity found.");
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
		}
	}

	@RequestMapping(value = OrderNaoConstants.PATH_ORDER_STATUS_MODAL)
	@ResponseBody
	public String showOrderStatusModal(@RequestParam(OrderNaoConstants.REQUEST_PARAM_ORDERNUMBER) String orderNumber,
			@RequestParam(OrderNaoConstants.REQUEST_PARAM_NEW_STATUS_OF_ORDER) String newStatusOfOrder,
			HttpSession session) {
		session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_NEW_ORDER_STATUS, newStatusOfOrder);
		session.setAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_ORDER_NUMBER, orderNumber);
		logger.info(
				"showOrderStatusModal Order Number :- " + orderNumber + " New status of order :- " + newStatusOfOrder);
		return OrderNaoConstants.RETURN_STATUS_SUCCESS;
	}

	@RequestMapping(value = OrderNaoConstants.PATH_SAVE_FAILED_STATUS_COMMENT, method = RequestMethod.POST)
	public String saveFailedStatusCommentsOfOrder(@RequestParam("failedComments") String failedComments,
			HttpSession session, Model model) {
		logger.info("Entry at saveFailedStatusCommentsOfOrder(Controller) Failed Comments :- " + failedComments);
		String orderNumber = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_ORDER_NUMBER);
		String newStatusOfOrder = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_NEW_ORDER_STATUS);
		if ((newStatusOfOrder != null && newStatusOfOrder.trim().length() > 0)
				&& newStatusOfOrder.equalsIgnoreCase(OrderNaoConstants.ORDER_STATUS_FAILED)) {
			boolean status = service.checkForPendingOrDeliveredOrder(orderNumber);
			if (status) {
				boolean savedCommentStatus = service.saveFailedStatusCommentsOfOrder(orderNumber, failedComments);
				if (savedCommentStatus) {
					logger.info("Exit at saveFailedStatusCommentsOfOrder(Controller)");
					List<OrderBean> orderBeanList = service.getOrderDetailsOfCustomer();
					List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, orderBeanList);
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
					logger.info("trackDeliveryTable(Controller)");
					return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
				} else {
					logger.info("Error(Controller)");
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
							OrderNaoConstants.ERROR_MSG_HEADER);
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE, OrderNaoConstants.ERROR_MSG);
					return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
				}
			} else {
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
						OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
						OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
				logger.info("Suspicious Activity found.");
				return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
			}

		} else {
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			logger.info("Suspicious Activity found.");
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
		}

	}

	@RequestMapping(value = OrderNaoConstants.PATH_SAVE_PENDING_OR_DELIVERED_ORDER_STATUS)
	public String savePendingOrDeliveredStatusCommentsOfOrder(HttpSession session, Model model) {
		logger.info("Entry at savePendingOrDeliveredStatusCommentsOfOrder(Controller) ");
		String orderNumber = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_ORDER_NUMBER);
		String newStatusOfOrder = (String) session.getAttribute(OrderNaoConstants.SESSION_ATTRIBUTE_NEW_ORDER_STATUS);
		if ((newStatusOfOrder != null && newStatusOfOrder.trim().length() > 0)
				&& (newStatusOfOrder.equalsIgnoreCase(OrderNaoConstants.ORDER_STATUS_DELIVERED)
						|| newStatusOfOrder.equalsIgnoreCase(OrderNaoConstants.ORDER_STATUS_PENDING))) {

			boolean status = service.checkForPendingOrDeliveredOrder(orderNumber);
			if (status) {
				boolean newOrderStatus = service.savePendingOrFailedOrderStatus(orderNumber, newStatusOfOrder);
				logger.info("Exit at saveFailedStatusCommentsOfOrder(Controller)");
				if (newOrderStatus) {
					List<OrderBean> orderBeanList = service.getOrderDetailsOfCustomer();
					List<OrderBean> deliveryBoysList = service.getDeliveryBoysList();
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_TRACK_ORDER_BEAN, orderBeanList);
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_DELIVERY_BOYS_BEAN, deliveryBoysList);
					logger.info("trackDeliveryTable(Controller)");
					return OrderNaoConstants.PATH_FRAGMENTS_TRACK_DELIVERY_TABLE;
				} else {
					logger.info("Error(Controller)");
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
							OrderNaoConstants.ERROR_MSG_HEADER);
					model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE, OrderNaoConstants.ERROR_MSG);
					return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
				}
			} else {
				logger.info("Suspicious Activity found.");
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
						OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
				model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
						OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
				return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
			}

		} else {
			logger.info("Suspicious Activity found.");
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE_HEADER,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG_HEADER);
			model.addAttribute(OrderNaoConstants.MODAL_ATTRIBUTE_ERROR_MESSAGE,
					OrderNaoConstants.SUSPICIOUS_ACTIVITY_MSG);
			return OrderNaoConstants.PATH_SUSPICIOUS_ACTIVITY;
		}
	}
}
