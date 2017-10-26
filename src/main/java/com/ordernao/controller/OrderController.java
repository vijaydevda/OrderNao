package com.ordernao.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ordernao.bean.EmployeeBean;
import com.ordernao.bean.OrderBean;
import com.ordernao.filter.AuthenticationFilter;
import com.ordernao.service.OrderService;

@Controller
public class OrderController {
	static final Logger logger = LogManager.getLogger(OrderController.class.getName());
	@Autowired
	OrderService service;

	@RequestMapping(value = "/summary.html")
	public String showHome(HttpServletRequest request, HttpServletResponse response) {
		return "summary";
	}

	@RequestMapping(value = "/trackDelivery.html")
	public String trackDelivery(HttpServletRequest request, HttpServletResponse response) {
		return "trackDelivery";
	}

	@RequestMapping(value = "/saveContactNumber", method = RequestMethod.POST)
	@ResponseBody
	public String saveContactNumber(@RequestParam("contactNumber") String contactNumber, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		logger.info("Inside SaveContactNumberMethod(Controller)");
		logger.debug("Contact Number :- " + contactNumber);
		if (contactNumber.isEmpty() || contactNumber.length() < 10 || contactNumber.length() > 10) {
			return "incorrectPhoneNumber";
		} else {
			session.invalidate();
			session = request.getSession();
			session.setAttribute("contactNumber", contactNumber);
			try {
				OrderBean orderBean = service.checkExistingCustomer(contactNumber);
				session.setAttribute("order", orderBean);
				boolean newCustomer = orderBean.isNewCustomer();
				if (newCustomer) {
					session.setAttribute("newCustomer", "TRUE");
				} else {
					session.setAttribute("newCustomer", "FALSE");
				}
				return "success";
			} catch (Exception e) {
				logger.error("Exception :- " + e);
				return "error";
			}
		}

	}

	@RequestMapping("/saveNewCustomerAndOrder")
	@ResponseBody
	public String saveNewCustomer(@ModelAttribute("order") OrderBean orderBean, HttpSession session) {
		logger.info("Inside saveNewCustomer(Controller) ");
		logger.info(orderBean.getCustomerFirstName() + "  " + orderBean.getCustomerLastName());
		String contactNumber = (String) session.getAttribute("contactNumber");
		String customerFirstName = orderBean.getCustomerFirstName();
		String itemName = orderBean.getItemName();
		String orderPickedFrom = orderBean.getOrderPickedFrom();
		String orderDeliveredAt = orderBean.getOrderDeliveredAt();
		int totalDistance = orderBean.getTotalDistance();
		double serviceCharge = orderBean.getServiceCharge();
		if (customerFirstName == null || customerFirstName.isEmpty()) {
			return "emptyFirstName";
		} else if (itemName == null || itemName.isEmpty()) {
			return "emptyItem";
		} else if (orderPickedFrom == null || orderPickedFrom.isEmpty()) {
			return "emptyPickupLocation";
		} else if (orderDeliveredAt == null || orderDeliveredAt.isEmpty()) {
			return "emptyDeliveryLocation";
		} else if (totalDistance < 0) {
			return "incorrectDistance";
		} else if (serviceCharge < 0) {
			return "incorrectServiceCharge";
		}
		orderBean.setCustomerPhone(contactNumber);

		try {
			boolean status = service.saveNewCustomer(orderBean);
			if (status) {
				return "success";
			} else {
				return "fail";
			}
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return "fail";
		}

	}

	@RequestMapping("/saveNewOrderForExistingCustomer")
	@ResponseBody
	public String saveNewOrderForExistingCustomer(@ModelAttribute("order") OrderBean orderBean, HttpSession session) {
		logger.info("Inside saveNewOrderForExistingCustomer(Controller) ");
		String contactNumber = (String) session.getAttribute("contactNumber");
		orderBean.setCustomerPhone(contactNumber);
		String itemName = orderBean.getItemName();
		String orderPickedFrom = orderBean.getOrderPickedFrom();
		String orderDeliveredAt = orderBean.getOrderDeliveredAt();
		int totalDistance = orderBean.getTotalDistance();
		double serviceCharge = orderBean.getServiceCharge();
		if (itemName == null || itemName.isEmpty()) {
			return "emptyItem";
		} else if (orderPickedFrom == null || orderPickedFrom.isEmpty()) {
			return "emptyPickupLocation";
		} else if (orderDeliveredAt == null || orderDeliveredAt.isEmpty()) {
			return "emptyDeliveryLocation";
		} else if (totalDistance < 0) {
			return "incorrectDistance";
		} else if (serviceCharge < 0) {
			return "incorrectServiceCharge";
		}

		try {
			boolean status = service.saveNewOrderForExistingCustomer(orderBean);
			if (status) {
				return "success";
			} else {
				return "fail";
			}
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return "fail";
		}

	}

	/*
	 * @RequestMapping("/checkForUsername")
	 * 
	 * @ResponseBody public String checkForUserName(@RequestParam("username")
	 * String userName) { if (userName == null || userName.isEmpty()) { return
	 * "emptyUsername"; }
	 * 
	 * try { boolean status = service.checkForUserName(userName); if (status) {
	 * return "success"; } else { return "fail"; } } catch (Exception e) {
	 * logger.error("Exception :- " + e); return "fail"; }
	 * 
	 * }
	 */

	@RequestMapping("/addEmployee")
	@ResponseBody
	public String addEmployee(@ModelAttribute("employeeBean") EmployeeBean employee) {
		String userName = employee.getUserName();
		String firstName = employee.getFirstName();
		String lastName = employee.getLastName();
		String password = employee.getPassword();
		String confirmPassword = employee.getConfirmPassword();
		String employeeType = employee.getEmployeeType();
		logger.info(userName+" "+firstName+" "+lastName+" "+password+" "+confirmPassword+" "+employeeType);
		if (userName == null || userName.isEmpty()) {
			return "emptyUsername";
		} else if (firstName == null || firstName.isEmpty()) {
			return "emptyFirstname";
		} else if (password == null || password.isEmpty()) {
			return "emptyPassword";
		} else if (confirmPassword == null || confirmPassword.isEmpty()) {
			return "emptyConfirmPassword";
		} else if (!password.equals(confirmPassword)) {
			return "passwordsDontMatch";
		}
		boolean usernameStatus = service.checkForUserName(userName);
		if (usernameStatus) {
			logger.info("Username Exists ");
			return "userNameNotAvailable";
		}
		boolean status = service.saveEmployee(employee);
		if (status) {
			logger.info("Employee Inserted successfully ");
			return "success";
		}
		logger.info("Employee Insertion failed ");
		return "fail";

	}

}
