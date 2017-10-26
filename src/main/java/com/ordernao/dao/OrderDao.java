package com.ordernao.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.ordernao.bean.EmployeeBean;
import com.ordernao.bean.OrderBean;
import com.ordernao.filter.AuthenticationFilter;

@Repository
public class OrderDao {
	static final Logger logger = LogManager.getLogger(OrderDao.class.getName());
	@Autowired
	JdbcTemplate jdbcTemplate;

	public int checkExistingCustomer(String contactNumber) {
		logger.info("Inside checkExistingCustomer(DAO) contactNumber" + contactNumber);
		String query = "SELECT COUNT(*) FROM ordernao.customer WHERE phone=?";
		logger.debug("Query : - " + query);
		int count = jdbcTemplate.queryForObject(query, new Object[] { contactNumber }, Integer.class);
		return count;
	}

	public OrderBean getExistingCustomerInfo(String contactNumber) {
		logger.info("Inside getExistingCustomerInfo(DAO)");
		String query = "SELECT customer.firstname,customer.lastname,customer.phone,customer.address,"
				+ "orders.comments,orders.commentstatus,orders.totalAmount,orderdetails.itemName,"
				+ "users.firstname,users.lastname FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
				+ "INNER JOIN users ON users.id=orders.DeliveryPersonId "
				+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
				+ "WHERE customer.phone=? ORDER BY orders.commentstatus DESC,orders.date DESC LIMIT 1";
		logger.debug("Query :- " + query);
		return jdbcTemplate.query(query, new Object[] { contactNumber }, new ResultSetExtractor<OrderBean>() {
			@Override
			public OrderBean extractData(ResultSet rs) throws SQLException, DataAccessException {
				OrderBean orderBean = new OrderBean();
				while (rs.next()) {
					orderBean.setCustomerFirstName(rs.getString(1));
					orderBean.setCustomerLastName(rs.getString(2));
					orderBean.setCustomerPhone(rs.getString(3));
					orderBean.setCustomerAddress(rs.getString(4));
					orderBean.setComments(rs.getString(5));
					orderBean.setCommentStatus(rs.getString(6));
					orderBean.setTotalAmount(rs.getDouble(7));
					orderBean.setItemName(rs.getString(8));
					orderBean.setUserFirstName(rs.getString(9));
					orderBean.setUserLastName(rs.getString(10));
				}
				return orderBean;
			}
		});
	}

	public OrderBean getNewCustomerInfo(String contactNumber) {
		logger.info("Inside getExistingCustomerInfo(DAO)");
		logger.debug("customer contact Number " + contactNumber);
		String query = "SELECT customer.firstname,customer.lastname,customer.phone,customer.address,"
				+ "customer.comment FROM customer WHERE customer.phone=? OR customer.alternate_phone=?";
		logger.debug("Query :- " + query);
		return jdbcTemplate.query(query, new Object[] { contactNumber, contactNumber },
				new ResultSetExtractor<OrderBean>() {
					@Override
					public OrderBean extractData(ResultSet rs) throws SQLException, DataAccessException {
						OrderBean orderBean = new OrderBean();
						while (rs.next()) {
							orderBean.setCustomerFirstName(rs.getString(1));
							orderBean.setCustomerLastName(rs.getString(2));
							orderBean.setCustomerPhone(rs.getString(3));
							orderBean.setCustomerAddress(rs.getString(4));
							orderBean.setComments(rs.getString(5));
						}
						logger.info("customer name :- " + orderBean.getCustomerFirstName());
						logger.info("customer phone :- " + orderBean.getCustomerPhone());
						return orderBean;
					}
				});
	}

	public int saveNewCustomerInfo(OrderBean orderBean) {
		logger.info("inside saveNewCustomer(DAO)");
		String customerFirstName = orderBean.getCustomerFirstName();
		String customerLastName = orderBean.getCustomerLastName();
		String customerAddress = orderBean.getCustomerAddress();
		String contactNumber = orderBean.getCustomerPhone();
		String password = "";
		int adminId = 1;
		String query = "INSERT INTO customer(username,firstname,lastname,password,phone,adminid,address) VALUES(?,?,?,?,?,?,?)";
		return jdbcTemplate.update(query, new Object[] { contactNumber, customerFirstName, customerLastName, password,
				contactNumber, adminId, customerAddress });
	}

	public int getCustomerIdFromContactNumber(String contactNumber) {
		String query = "SELECT id FROM customer WHERE phone=?";
		int id = (Integer) jdbcTemplate.queryForObject(query, new Object[] { contactNumber }, Integer.class);
		logger.debug("id of customer :- " + id + " with phone number :- " + query);
		return id;
	}

	public int saveNewOrderInfoForExistingCustomerInOrderDetailsTable(OrderBean orderBean, int orderNumber) {
		logger.info("inside saveNewOrderInfoForExistingCustomerInOrderDetailsTable(DAO)");
		String itemName = orderBean.getItemName();
		String orderPickedFrom = orderBean.getOrderPickedFrom();
		String orderDeliveredAt = orderBean.getOrderDeliveredAt();
		logger.info(orderPickedFrom + " " + orderDeliveredAt);
		String query = "INSERT INTO orderdetails(orderno,itemName,pickuppoint,deliverypoint) VALUES(?,?,?,?)";
		return jdbcTemplate.update(query, new Object[] { orderNumber, itemName, orderPickedFrom, orderDeliveredAt });
	}

	public int saveNewOrderInfoForExistingCustomerInOrdersTable(OrderBean orderBean) {
		logger.info("inside saveNewOrderInfoForExistingCustomerInOrdersTable(DAO)");
		int totalDistance = orderBean.getTotalDistance();
		double serviceCharge = orderBean.getServiceCharge();
		int id = orderBean.getCustomerId();
		String comments = orderBean.getComments();
		String commentStatus = orderBean.getCommentStatus();
		logger.info(totalDistance + ":::::: " + serviceCharge);
		String query = "INSERT INTO orders(clientid,totalDistance,serviceCharge,comments,commentstatus) VALUES(?,?,?,?,?)";
		return jdbcTemplate.update(query, new Object[] { id, totalDistance, serviceCharge, comments, commentStatus });
	}
	/*
	 * public int saveComments(String comments) { String query="insert into cus"
	 * }
	 */

	public int getOrderNumberFromClientId(int id) {
		String query = "SELECT orderno FROM orders WHERE clientid=? ORDER BY orderno DESC LIMIT 1";
		int orderNumber = (Integer) jdbcTemplate.queryForObject(query, new Object[] { id }, Integer.class);
		logger.debug("id of customer :- " + id + " with phone number :- " + query);
		return orderNumber;
	}

	public int saveEmployee(EmployeeBean employee, int employeeTypeId) {
		String userName = employee.getUserName();
		String firstName = employee.getFirstName();
		String lastName = employee.getLastName();
		String password = employee.getPassword();
		String email = employee.getEmail();
		String query = "INSERT INTO USERS(username,firstname,lastname,PASSWORD,email,roleid) VALUES(?,?,?,?,?,?)";
		return jdbcTemplate.update(query, new Object[] { userName, firstName, lastName, password, email,employeeTypeId });
	}

	public int checkForUserName(String userName) {
		String query="select COUNT(*) from users where userName=?";
		int result = (Integer) jdbcTemplate.queryForObject(query, new Object[] { userName}, Integer.class);
		return result;
	}

	/*
	 * public int checkPreviousOrderForExistingCustomer(int id) {
	 * logger.info("Inside checkPreviousOrderForExistingCustomer"); String query
	 * = "SELECT COUNT(*) FROM orderdetails WHERE clientid=?"; int orderNumber =
	 * (Integer) jdbcTemplate.queryForObject(query, new Object[] { id },
	 * Integer.class); logger.debug("Query : - " + query); int count =
	 * jdbcTemplate.queryForObject(query, new Object[] { id }, Integer.class);
	 * return count; }
	 */

}
