package com.ordernao.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ordernao.bean.EmployeeBean;
import com.ordernao.bean.OrderBean;
import com.ordernao.dao.OrderDao;
import com.ordernao.filter.AuthenticationFilter;

@Service
public class OrderService {
	static final Logger logger = LogManager.getLogger(OrderService.class.getName());
	@Autowired
	OrderDao dao;

	public OrderBean checkExistingCustomer(String contactNumber) {
		logger.info("Inside checkExistingCustomer(service) ");
		OrderBean orderBean = new OrderBean();
		int result = dao.checkExistingCustomer(contactNumber);
		// If status > 0 then customer exist else not exist(i.e new customer)
		if (result > 0) {
			logger.info("customer exist fetching customer details");
			orderBean = dao.getExistingCustomerInfo(contactNumber);
			orderBean.setNewCustomer(false);
		} else {
			logger.info("customer doesn't exist");
			orderBean.setNewCustomer(true);
		}
		return orderBean;
	}

	public boolean saveNewCustomer(OrderBean orderBean) {
		logger.info("inside saveNewCustomer(Service)");
		boolean status = false;
		String contactNumber = orderBean.getCustomerPhone();
		int customerStatus = dao.saveNewCustomerInfo(orderBean);
		if (customerStatus > 0) {
			logger.info("new customer registered:-");
			int id = dao.getCustomerIdFromContactNumber(contactNumber);
			orderBean.setCustomerId(id);
			String comments = orderBean.getComments();
			comments.replaceAll("\\s", "");
			if (!comments.isEmpty()) {
				logger.info("Comment Status Failed");
				orderBean.setCommentStatus("Failed ");
			} else {
				logger.info("Comment Status N/A");
			}
			int orderStatus1 = dao.saveNewOrderInfoForExistingCustomerInOrdersTable(orderBean);
			if (orderStatus1 > 0) {
				int orderNumber = dao.getOrderNumberFromClientId(id);
				int orderStatus2 = dao.saveNewOrderInfoForExistingCustomerInOrderDetailsTable(orderBean, orderNumber);
				if (orderStatus2 > 0) {
					logger.info("customer new order registered...");
					status = true;
				} else {
					status = false;
				}
			} else {
				status = false;
			}
		} else {
			status = false;
		}
		return status;
	}

	public boolean saveNewOrderForExistingCustomer(OrderBean orderBean) {
		logger.info("inside saveNewOrderForExisting Customer(Service)");
		boolean status = false;
		String contactNumber = orderBean.getCustomerPhone();
		int id = dao.getCustomerIdFromContactNumber(contactNumber);
		orderBean.setCustomerId(id);
		String comments = orderBean.getComments();
		comments.replaceAll("\\s", "");
		if (!comments.isEmpty()) {
			logger.info("Comment Status Failed");
			orderBean.setCommentStatus("Failed ");
		} else {
			logger.info("Comment Status N/A");
		}
		int orderStatus1 = dao.saveNewOrderInfoForExistingCustomerInOrdersTable(orderBean);
		if (orderStatus1 > 0) {
			int orderNumber = dao.getOrderNumberFromClientId(id);
			int orderStatus2 = dao.saveNewOrderInfoForExistingCustomerInOrderDetailsTable(orderBean, orderNumber);
			if (orderStatus2 > 0) {
				logger.info("customer new order registered...");
				status = true;
			} else {
				status = false;
			}
		} else {
			status = false;
		}
		return status;
	}

	/*
	 * public boolean checkPreviousOrderForExistingCustomer(String
	 * contactNumber) {
	 * logger.info("Inside checkPreviousOrderForExistingCustomer"); boolean
	 * status=false; int
	 * customerId=dao.getCustomerIdFromContactNumber(contactNumber); int
	 * order=dao.checkPreviousOrderForExistingCustomer(customerId); if(order >
	 * 0){ status=true; }else{ status=false; } return status; }
	 */
	public OrderBean getNewCustomerInfo(String contactNumber) {
		logger.info("Inside getNewCustomerInfo(Service)");
		return dao.getNewCustomerInfo(contactNumber);
	}

	public boolean saveEmployee(EmployeeBean employee) {
		boolean status = false;
		String employeeType = employee.getEmployeeType();
		logger.info("Employee Type: " + employeeType);
		int employeeTypeId = -1;
		if (employeeType.equals("manager")) {
			employeeTypeId = 1;
		} else if (employeeType.equals("callOperator")) {
			employeeTypeId = 2;
		} else if (employeeType.equals("deliveryBoys")) {
			employeeTypeId = 3;
		}

		try {
			int result = dao.saveEmployee(employee, employeeTypeId);
			if (result > 0) {
				logger.info("Employee inserted Successfully...");
				status = true;
			} else {
				status = false;
			}
			return status;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return status;
		}

	}

	public boolean checkForUserName(String userName) {
		boolean status = false;
		try {
			int result = dao.checkForUserName(userName);
			logger.info("checkForUserName :- "+result);
			if (result > 0) {
				status = true;
			} else {
				status = false;
			}
			return status;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return true;
		}

	}

}
