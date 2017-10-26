package com.ordernao.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ordernao.bean.EmployeeBean;
import com.ordernao.bean.OrderBean;
import com.ordernao.dao.OrderDao;
import com.ordernao.utility.OrderNaoConstants;
import com.ordernao.utility.Utility;

@Service
public class OrderService {
	static final Logger logger = LogManager.getLogger(OrderService.class.getName());
	@Autowired
	OrderDao dao;

	public OrderBean checkExistingCustomer(String contactNumber) {
		logger.info("Entry at checkExistingCustomer(Service)");
		OrderBean orderBean = new OrderBean();
		try {
			int result = dao.checkExistingCustomer(contactNumber);
			if (result > OrderNaoConstants.CUSTOMER_EXIST) {
				logger.info("Customer exist fetching customer details");
				orderBean = dao.getExistingCustomerInfo(contactNumber);
				orderBean.setNewCustomer(false);
			} else {
				logger.info("Customer doesn't exist");
				orderBean.setNewCustomer(true);
			}
			logger.info("Exit at checkExistingCustomer(Service)");
			return orderBean;
		} catch (Exception e) {
			logger.error("Exception :- ", e);
			return orderBean;
		}

	}

	public boolean saveNewCustomer(OrderBean orderBean) {
		logger.info("Entry at saveNewCustomer(Service)");
		boolean status = false;
		int customerStatus = dao.saveNewCustomerInfo(orderBean);
		if (customerStatus > 0) {
			status = insertOrderDetails(orderBean);
		}
		logger.info("Exit at saveNewCustomer(Service)");
		return status;
	}

	public boolean insertOrderDetails(OrderBean orderBean) {
		logger.info("Entry at insertOrderDetails(Service)");
		boolean status = false;
		String contactNumber = orderBean.getCustomerPhone();
		int id = dao.getCustomerIdFromContactNumber(contactNumber);
		orderBean.setCustomerId(id);
		String comments = orderBean.getComments();
		comments.replaceAll("\\s", "");
		if (!comments.isEmpty()) {
			logger.info("Comment Status :- Failed");
			orderBean.setCommentStatus(OrderNaoConstants.COMMENT_STATUS_FAILED);
		} else {
			logger.info("Comment Status :- N/A");
		}
		int orderStatus1 = dao.saveNewOrderInfoForCustomerInOrdersTable(orderBean);
		if (orderStatus1 > 0) {
			int orderNumber = dao.getOrderNumberFromClientId(id);
			int totalOrders = orderBean.getTotalNumberOfOrders();
			String[] itemNamesArray = Utility.getArrayFromString(orderBean.getItemName());
			String[] orderPickupPointsArray = Utility.getArrayFromString(orderBean.getOrderPickedFrom());
			String[] orderDeliveredAtArray = Utility.getArrayFromString(orderBean.getOrderDeliveredAt());
			if (totalOrders > 1) {
				// Here total order is number of pickup points and delivery
				// points
				// Multiple if totalOrders is > 1 else only one
				int[] mulipleOrderStatus = dao.saveMultipleOrders(orderBean, orderNumber, itemNamesArray,
						orderPickupPointsArray, orderDeliveredAtArray);
				if (mulipleOrderStatus.length > 1) {
					status = true;
				} else {
					status = false;
				}
			} else {
				int singleOrderStatus = dao.saveNewOrderInfoForCustomerInOrderDetailsTable(orderBean, orderNumber,
						itemNamesArray, orderPickupPointsArray, orderDeliveredAtArray);
				if (singleOrderStatus > 0) {
					status = true;
				} else {
					status = false;
				}
			}
		} else {
			status = false;
		}
		logger.info("Exit at saveNewCustomer(Service)");
		return status;
	}

	public OrderBean getNewCustomerInfo(String contactNumber) {
		logger.info("Entry at getNewCustomerInfo(Service)");
		logger.info("Exit at getNewCustomerInfo(Service)");
		return dao.getNewCustomerInfo(contactNumber);
	}

	public boolean saveEmployee(EmployeeBean employee) {
		logger.info("Entry at saveEmployee(Service)");
		boolean status = false;
		String employeeType = employee.getEmployeeType();
		logger.info("Employee Type :-(1-admin)(2-manager)(3-call operator)(4-delivery boy) " + employeeType);
		int employeeTypeId = OrderNaoConstants.INVALID_EMPLOYEE_TYPE;
		if (employeeType.equals(OrderNaoConstants.ADMIN)) {
			employeeTypeId = OrderNaoConstants.ADMIN_TYPE;
		}else if (employeeType.equals(OrderNaoConstants.MANAGER)) {
			employeeTypeId = OrderNaoConstants.MANAGER_TYPE;
		} else if (employeeType.equals(OrderNaoConstants.CALL_OPERATOR)) {
			employeeTypeId = OrderNaoConstants.CALL_OPERATOR_TYPE;
		} else if (employeeType.equals(OrderNaoConstants.DELIVERY_BOY)) {
			employeeTypeId = OrderNaoConstants.DELIVERY_BOY_TYPE;
		}

		try {
			int result = dao.saveEmployee(employee, employeeTypeId);
			if (result > 0) {
				logger.info("Employee inserted Successfully...");
				status = true;
			} else {
				status = false;
			}
			logger.info("Exit at saveEmployee(Service)");
			return status;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return status;
		}

	}

	public boolean checkForUserName(String userName) {
		logger.info("Entry at checkForUserName(Service)");
		boolean status = false;
		try {
			int result = dao.checkForUserName(userName);
			logger.info("checkForUserName :- " + result);
			if (result > 0) {
				status = true;
			} else {
				status = false;
			}
			logger.info("Exit at checkForUserName(Service)");
			return status;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return true;
		}

	}

	public List<OrderBean> getOrderDetailsOfCustomer() {
		return dao.getOrderDetailsOfCustomer();
	}

	public List<OrderBean> getDeliveryBoysList() {
		return dao.getDeliveryBoysList();
	}

	public boolean assignOrderToDeliveryBoy(int deliveryBoyId, int orderNumber) {
		logger.info("Entry at assignOrderToDeliveryBoy(Service)");
		boolean status = false;
		int result = dao.updateDeliveryBoyInOrdersTable(deliveryBoyId, orderNumber);
		if (result > 0) {
			status = true;
		}
		logger.info("Exit at assignOrderToDeliveryBoy(Service)");
		return status;
	}

	public List<OrderBean> getMoreDetailOfOrder(int orderNumber) {
		logger.info("Entry at getMoreDetailOfOrder(Service)");
		List<OrderBean> moreDetailsOfOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at getMoreDetailOfOrder(Service)");
			moreDetailsOfOrderList = dao.getMoreDetailOfOrder(orderNumber);
			return moreDetailsOfOrderList;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return moreDetailsOfOrderList;
		}

	}

	public List<OrderBean> filterOrdersByStatus(String status) {
		logger.info("Entry at filterOrdersByStatus(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByStatus(Service)");
			filteredOrderList = dao.filterOrdersByStatus(status);
			return filteredOrderList;

		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}

	public List<OrderBean> filterOrdersByDate(String date) {
		logger.info("Entry at filterOrdersByDate(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByDate(Service)");
			filteredOrderList = dao.filterOrdersByDate(date);
			return filteredOrderList;

		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}

	public List<OrderBean> filterOrdersByAssignment(String assignment) {
		logger.info("Entry at filterOrdersByAssignment(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByAssignment(Service)");
			filteredOrderList = dao.filterOrdersByAssignment(assignment);
			return filteredOrderList;

		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}

	public List<OrderBean> searchTrackDelivery(String searchKey) {
		logger.info("Entry at searchTrackDelivery(Service)");
		List<OrderBean> searchOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at searchTrackDelivery(Service)");
			searchOrderList = dao.searchTrackDelivery(searchKey);
			return searchOrderList;

		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return searchOrderList;
		}
	}

	public OrderBean getCommentsOfFailedOrder(String orderNumber) {
		logger.info("Entry at getCommentsOfFailedOrder(Service)");
		OrderBean bean = dao.getCommentsOfFailedOrder(orderNumber);
		logger.info("Exit at getCommentsOfFailedOrder(Service)");
		return bean;
	}

	public boolean checkForOrderNumberAndFailedStatus(String orderNumber) {
		logger.info("Entry at checkForOrderNumberAndFailedStatus(Service)");
		int result = dao.checkForOrderNumberAndFailedStatus(orderNumber);
		if (result > 0) {
			logger.info("Exit at checkForOrderNumberAndFailedStatus(Service) :- Status Exist ");
			return true;
		} else {
			logger.info("Exit at checkForOrderNumberAndFailedStatus(Service) :- Status Doesn't Exist ");
			return false;
		}
	}

	public boolean checkForPendingOrDeliveredOrder(String orderNumber) {
		logger.info("Entry at checkForPendingOrFailedOrder(Service)");
		int result = dao.checkForPendingOrDeliveredOrder(orderNumber);
		if (result > 0) {
			logger.info("Exit at checkForPendingOrFailedOrder(Service)");
			return true;
		} else {
			logger.info("Exit at checkForPendingOrFailedOrder(Service)");
			return false;
		}
	}

	public boolean saveFailedStatusCommentsOfOrder(String orderNumber, String failedComments) {
		logger.info("Entry at saveFailedStatusCommentsOfOrder(Service)");
		int result = dao.saveFailedStatusCommentsOfOrder(orderNumber, failedComments);
		if (result > 0) {
			logger.info("Exit at saveFailedStatusCommentsOfOrder(Service)");
			return true;
		} else {
			logger.info("Exit at saveFailedStatusCommentsOfOrder(Service)");
			return false;
		}
	}

	public boolean savePendingOrFailedOrderStatus(String orderNumber, String newStatusOfOrder) {
		logger.info("Entry at savePendingOrFailedOrderStatus(Service)");
		int result = dao.savePendingOrFailedOrderStatus(orderNumber, newStatusOfOrder);
		if (result > 0) {
			logger.info("Exit at savePendingOrFailedOrderStatus(Service)");
			return true;
		} else {
			logger.info("Exit at savePendingOrFailedOrderStatus(Service)");
			return false;
		}
	}

	public boolean checkDeliveryBoyIdInDB(int deliveryBoyId) {
		logger.info("Entry at savePendingOrFailedOrderStatus(Service)");
		int result = dao.checkDeliveryBoyIdInDB(deliveryBoyId);
		if (result > 0) {
			logger.info("Exit at checkDeliveryBoyIdInDB(Service)");
			return true;
		} else {
			logger.info("Exit at checkDeliveryBoyIdInDB(Service)");
			return false;
		}
	}

	public List<OrderBean> filterOrdersByStatusAndTime(String date, String status) {
		logger.info("Entry at filterOrdersByStatusAndTime(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByStatusAndTime(Service)");
			filteredOrderList = dao.filterOrdersByStatusAndTime(date, status);
			return filteredOrderList;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}

	public List<OrderBean> filterOrdersByTimeAndAssignment(String time, String assignment) {
		logger.info("Entry at filterOrdersByTimeAndAssignment(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByTimeAndAssignment(Service)");
			filteredOrderList = dao.filterOrdersByTimeAndAssignment(time, assignment);
			return filteredOrderList;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}

	public List<OrderBean> filterOrdersByStatusAndAssignment(String status, String assignment) {
		logger.info("Entry at filterOrdersByStatusAndAssignment(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByStatusAndAssignment(Service)");
			filteredOrderList = dao.filterOrdersByStatusAndAssignment(status, assignment);
			return filteredOrderList;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}

	public List<OrderBean> filterOrdersByStatusAndTimeAndAssignment(String status, String time, String assignment) {
		logger.info("Entry at filterOrdersByStatusAndTimeAndAssignment(Service)");
		List<OrderBean> filteredOrderList = new ArrayList<OrderBean>();
		try {
			logger.info("Exit at filterOrdersByStatusAndTimeAndAssignment(Service)");
			filteredOrderList = dao.filterOrdersByStatusAndTimeAndAssignment(status, time, assignment);
			return filteredOrderList;
		} catch (Exception e) {
			logger.error("Exception :- " + e);
			return filteredOrderList;
		}
	}
}
