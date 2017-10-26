package com.ordernao.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ordernao.bean.EmployeeBean;
import com.ordernao.bean.OrderBean;
import com.ordernao.properties.QueriesProperty;
import com.ordernao.rowmapper.TrackOrderRowMapper;
import com.ordernao.utility.OrderNaoConstants;

@Repository
public class OrderDao {
	static final Logger logger = LogManager.getLogger(OrderDao.class.getName());
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private QueriesProperty property;

	public int checkExistingCustomer(String contactNumber) {
		logger.info("Entry at :- checkExistingCustomer(DAO)");
		logger.info("Contact Number  :- " + contactNumber);
		String sqlQuery = property.getProperty(OrderNaoConstants.CHECK_EXISTING_CUSTOMER);
		logger.info("Query :- " + sqlQuery);
		int count = jdbcTemplate.queryForObject(sqlQuery, new Object[] { contactNumber }, Integer.class);
		logger.info("Exit at :- checkExistingCustomer(DAO)");
		return count;
	}

	public OrderBean getExistingCustomerInfo(String contactNumber) {
		logger.info("Entry at getExistingCustomerInfo(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_EXISTING_CUSTOMER_INFO);
		logger.info("Exit at getExistingCustomerInfo(DAO)");
		return jdbcTemplate.queryForObject(sqlQuery, new Object[] { contactNumber }, new RowMapper<OrderBean>() {
			@Override
			public OrderBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrderBean orderBean = new OrderBean();
				orderBean.setCustomerFirstName(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_FIRSTNAME));
				orderBean.setCustomerLastName(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_LASTNAME));
				orderBean.setCustomerPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_PHONE));
				orderBean.setCustomerAddress(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_ADDRESS));
				orderBean.setComments(rs.getString(OrderNaoConstants.COLUMN_NAME_ORDER_COMMENTS));
				orderBean.setCommentStatus(rs.getString(OrderNaoConstants.COLUMN_NAME_ORDER_COMMENT_STATUS));
				orderBean.setTotalAmount(rs.getDouble(OrderNaoConstants.COLUMN_NAME_TOTAL_AMOUNT));
				orderBean.setItemName(rs.getString(OrderNaoConstants.COLUMN_NAME_ITEM_NAME));
				orderBean.setUserFirstName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_FIRSTNAME));
				orderBean.setUserLastName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_LASTNAME));
				logger.info(orderBean.getCustomerFirstName());
				return orderBean;
			}
		});
	}

	public OrderBean getNewCustomerInfo(String contactNumber) {
		logger.info("Entry at getExistingCustomerInfo(DAO)");
		logger.debug("Customer contact Number " + contactNumber);
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_NEW_CUSTOMER_INFO);
		logger.info("Exit at getExistingCustomerInfo(DAO)");
		return jdbcTemplate.queryForObject(sqlQuery, new Object[] { contactNumber, contactNumber },
				new RowMapper<OrderBean>() {
					@Override
					public OrderBean mapRow(ResultSet rs, int rowNum) throws SQLException {
						OrderBean orderBean = new OrderBean();
						orderBean.setCustomerFirstName(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_FIRSTNAME));
						orderBean.setCustomerLastName(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_LASTNAME));
						orderBean.setCustomerPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_PHONE));
						orderBean.setCustomerAddress(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_ADDRESS));
						orderBean.setComments(rs.getString(OrderNaoConstants.COLUMN_NAME_ORDER_COMMENTS));
						return orderBean;
					}
				});
	}

	public int saveNewCustomerInfo(OrderBean orderBean) {
		logger.info("Entry at saveNewCustomer(DAO)");
		String customerFirstName = orderBean.getCustomerFirstName();
		String customerLastName = orderBean.getCustomerLastName();
		String customerAddress = orderBean.getCustomerAddress();
		String contactNumber = orderBean.getCustomerPhone();
		String sqlQuery = property.getProperty(OrderNaoConstants.SET_NEW_CUSTOMER_DETAILS);
		logger.info("Exit at saveNewCustomer(DAO)");
		return jdbcTemplate.update(sqlQuery, new Object[] { contactNumber, customerFirstName, customerLastName,
				OrderNaoConstants.EMPTY_PASSWORD, contactNumber, OrderNaoConstants.ADMIN_ID, customerAddress });
	}

	public int getCustomerIdFromContactNumber(String contactNumber) {
		logger.info("Entry at getCustomerIdFromContactNumber(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_CUSTOMER_ID_FROM_CONTACT_NUMER);
		int id = (Integer) jdbcTemplate.queryForObject(sqlQuery, new Object[] { contactNumber }, Integer.class);
		logger.debug("Customer phone number :- " + contactNumber + " & customer id :- " + id);
		logger.info("Exit at getCustomerIdFromContactNumber(DAO)");
		return id;
	}

	public int saveNewOrderInfoForCustomerInOrderDetailsTable(OrderBean orderBean, int orderNumber,
			String[] itemNamesArray, String[] orderPickupPointsArray, String[] orderDeliveredAtArray) {
		logger.info("Entry at saveNewOrderInfoForExistingCustomerInOrderDetailsTable(DAO)");
		String sqlQuery = property
				.getProperty(OrderNaoConstants.SAVE_NEW_ORDER_INFO_FOR_CUSTOMER_IN_ORDERDETAILS_TABLE);
		logger.info("Exit at saveNewOrderInfoForExistingCustomerInOrderDetailsTable(DAO)");
		return jdbcTemplate.update(sqlQuery,
				new Object[] { orderNumber, itemNamesArray[0], orderPickupPointsArray[0], orderDeliveredAtArray[0] });
	}

	public int saveNewOrderInfoForCustomerInOrdersTable(OrderBean orderBean) {
		logger.info("Entry at saveNewOrderInfoForExistingCustomerInOrdersTable(DAO)");
		String commentStatus = orderBean.getCommentStatus();
		String status;
		if (OrderNaoConstants.COMMENT_STATUS_FAILED.equals(commentStatus)) {
			status = OrderNaoConstants.ORDER_STATUS_FAILED;
		} else {
			status = OrderNaoConstants.DEFAULT_ORDER_STATUS_PENDING;
		}
		int totalDistance = orderBean.getTotalDistance();
		double serviceCharge = orderBean.getServiceCharge();
		int id = orderBean.getCustomerId();
		String comments = orderBean.getComments();
		String sqlQuery = property.getProperty(OrderNaoConstants.SAVE_NEW_ORDER_INFO_FOR_CUSTOMER_IN_ORDERS_TABLE);
		logger.info("Exit at saveNewOrderInfoForExistingCustomerInOrdersTable(DAO)");
		return jdbcTemplate.update(sqlQuery,
				new Object[] { id, totalDistance, serviceCharge, comments, commentStatus, status });
	}

	public int getOrderNumberFromClientId(int id) {
		logger.info("Entry at getOrderNumberFromClientId(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.FETCH_ORDERNUMBER_FROM_CUSTOMER_ID);
		int orderNumber = (Integer) jdbcTemplate.queryForObject(sqlQuery, new Object[] { id }, Integer.class);
		logger.debug("Customer id :- " + id + " Customer order number :- " + orderNumber);
		logger.info("Exit at getOrderNumberFromClientId(DAO)");
		return orderNumber;
	}

	public int saveEmployee(EmployeeBean employee, int employeeTypeId) {
		logger.info("Entry at saveEmployee(DAO)");
		String userName = employee.getUserName();
		String firstName = employee.getFirstName();
		String lastName = employee.getLastName();
		String contactNumber = employee.getContactNumber();
		String password = employee.getPassword();
		String email = employee.getEmail();
		String sqlQuery = property.getProperty(OrderNaoConstants.SAVE_NEW_EMPLOYEE);
		logger.info("Exit at saveEmployee(DAO)");
		return jdbcTemplate.update(sqlQuery,
				new Object[] { userName, firstName, lastName, password, email, employeeTypeId, contactNumber });
	}

	public int checkForUserName(String userName) {
		logger.info("Entry at checkForUserName(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.CHECK_FOR_USERNAME);
		int result = (Integer) jdbcTemplate.queryForObject(sqlQuery, new Object[] { userName }, Integer.class);
		logger.info("Exit at checkForUserName(DAO)");
		return result;
	}

	public int[] saveMultipleOrders(OrderBean orderBean, int orderNumber, String[] itemNamesArray,
			String[] orderPickupPointsArray, String[] orderDeliveredAtArray) {

		logger.info("Entry at saveMultipleOrders(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.SAVE_MULTIPLE_ORDERS);
		logger.info("Exit at saveMultipleOrders(DAO)");
		return jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, orderNumber);
				ps.setString(2, itemNamesArray[i]);
				ps.setString(3, orderPickupPointsArray[i]);
				ps.setString(4, orderDeliveredAtArray[i]);
			}

			@Override
			public int getBatchSize() {
				return (orderBean.getTotalNumberOfOrders());
			}
		});

	}

	public List<OrderBean> getOrderDetailsOfCustomer() {
		logger.info("Entry at getOrderDetailsOfCustomer(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_ORDER_DETAILS_OF_CUSTOMER);
		logger.info("Exit at getOrderDetailsOfCustomer(DAO)");
		return jdbcTemplate.query(sqlQuery, new TrackOrderRowMapper());
	}

	public List<OrderBean> getDeliveryBoysList() {
		logger.info("Entry at getDeliveryBoysList(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_DELIVERY_BOYS_LIST);
		logger.info("Exit at getDeliveryBoysList(DAO) Sql is:- " + sqlQuery);
		return jdbcTemplate.query(sqlQuery, new RowMapper<OrderBean>() {
			@Override
			public OrderBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrderBean orderBean = new OrderBean();
				orderBean.setDeliveryBoyId(rs.getInt(OrderNaoConstants.COLUMN_NAME_USERID));
				orderBean.setDeliveryBoyFirstName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_FIRSTNAME));
				orderBean.setDeliveryBoyLastName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_LASTNAME));
				orderBean.setDeliveryBoyPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_PHONE));
				return orderBean;
			}
		});
	}

	public int updateDeliveryBoyInOrdersTable(int deliveryBoyId, int orderNumber) {
		logger.info("Entry at updateDeliveryBoyInOrdersTable(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.UPDATE_DELIVERY_BOYS);
		logger.info("Exit at updateDeliveryBoyInOrdersTable(DAO)");
		return jdbcTemplate.update(sqlQuery, new Object[] { deliveryBoyId, orderNumber });
	}

	public List<OrderBean> getMoreDetailOfOrder(int orderNumber) {
		logger.info("Entry at getMoreDetailOfOrder(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_MORE_DETAILS_OF_ORDER);
		logger.info("Query :- " + sqlQuery);
		logger.info("Exit at getMoreDetailOfOrder(DAO)");
		return jdbcTemplate.query(sqlQuery, new Object[] { orderNumber }, new RowMapper<OrderBean>() {
			@Override
			public OrderBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrderBean orderBean = new OrderBean();
				orderBean.setCustomerPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_PHONE));
				orderBean.setOrderNumber(rs.getInt(OrderNaoConstants.COLUMN_NAME_ORDER_NUMBER));
				orderBean.setOrderDate(rs.getDate(OrderNaoConstants.COLUMN_NAME_ORDER_DATE));
				orderBean.setOrderStatus(rs.getString(OrderNaoConstants.COLUMN_NAME_ORDER_STATUS));
				orderBean.setOrderPickedFrom(rs.getString(OrderNaoConstants.COLUMN_NAME_PICKUP_POINT));
				orderBean.setOrderDeliveredAt(rs.getString(OrderNaoConstants.COLUMN_NAME_DELIVERY_POINT));
				orderBean.setItemName(rs.getString(OrderNaoConstants.COLUMN_NAME_ITEM_NAME));
				orderBean.setTotalDistance(rs.getInt(OrderNaoConstants.COLUMN_NAME_TOTAL_DISTANCE));
				orderBean.setProductCharge(rs.getDouble(OrderNaoConstants.COLUMN_NAME_PRODUCT_CHARGE));
				orderBean.setServiceCharge(rs.getDouble(OrderNaoConstants.COLUMN_NAME_SERVICE_CHARGE));
				orderBean.setDeliveryBoyId(rs.getInt(OrderNaoConstants.COLUMN_NAME_USERID));
				orderBean.setDeliveryBoyFirstName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_FIRSTNAME));
				orderBean.setDeliveryBoyLastName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_LASTNAME));
				orderBean.setDeliveryBoyPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_PHONE));
				return orderBean;
			}
		});
	}

	public List<OrderBean> filterOrdersByStatus(String status) {
		logger.info("Entry at filterOrdersByStatus(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.FILTER_ORDER_BY_STATUS);
		logger.info("Exit at filterOrdersByStatus(DAO)");
		return jdbcTemplate.query(sqlQuery, new Object[] { status }, new TrackOrderRowMapper());
	}

	public List<OrderBean> filterOrdersByDate(String date) {
		logger.info("Entry at filterOrdersByDate(DAO)");
		StringBuilder query = new StringBuilder(
				"SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,"
						+ "orders.status,orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,"
						+ "orders.totalDistance,orders.productCharge,orders.serviceCharge,users.id,users.firstname,users.lastname,users.phone "
						+ "FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
						+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
						+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE ");
		if (date.equals(OrderNaoConstants.ORDER_DATE_TODAY)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TODAY);
		} else if (date.equals(OrderNaoConstants.ORDER_DATE_LAST_SEVEN_DAYS)) {
			query.append(OrderNaoConstants.MYSQL_DATE_LAST_SEVEN_DAYS);
		} else if (date.equals(OrderNaoConstants.ORDER_DATE_THIS_MONTH)) {
			query.append(OrderNaoConstants.MYSQL_DATE_THIS_MONTH);
		} else if (date.equals(OrderNaoConstants.ORDER_DATE_TILL_DATE)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TILL_DATE);
		}
		query.append(
				" GROUP BY orderdetails.orderno ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC");
		logger.info("Query :- " + query);
		logger.info("Exitat filterOrdersByDate(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}

	public List<OrderBean> filterOrdersByAssignment(String assignment) {
		logger.info("Entry at filterOrdersByAssignment(DAO)");
		StringBuffer query = new StringBuffer(
				"SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,"
						+ "orders.status,orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,"
						+ "orders.totalDistance,orders.serviceCharge,orders.productCharge,users.id,users.firstname,users.lastname,users.phone "
						+ "FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
						+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
						+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE ");
		if (assignment.equals(OrderNaoConstants.ORDER_ASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NOT_NULL);
		} else if (assignment.equals(OrderNaoConstants.ORDER_UNASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NULL);
		}
		query.append(
				" GROUP BY orderdetails.orderno ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC");
		logger.info("Exit at filterOrdersByAssignment(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}

	public List<OrderBean> searchTrackDelivery(String searchKey) {
		logger.info("Entry at searchTrackDelivery(DAO)");
		String queryLikeStatement = "'%" + searchKey + "%'";
		String query = "SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,orders.status,"
				+ "orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,orders.totalDistance,orders.serviceCharge,orders.productCharge"
				+ ",users.id,users.firstname,users.lastname,users.phone FROM orders "
				+ "INNER JOIN customer ON customer.id=orders.clientid "
				+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
				+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE (customer.phone LIKE "
				+ queryLikeStatement + " OR orderdetails.pickuppoint LIKE " + queryLikeStatement + " OR "
				+ "orderdetails.deliverypoint LIKE " + queryLikeStatement + " OR orders.totalDistance LIKE "
				+ queryLikeStatement + " OR " + "orderdetails.itemName LIKE " + queryLikeStatement
				+ " OR orders.serviceCharge LIKE " + queryLikeStatement + " OR orders.productCharge LIKE "
				+ queryLikeStatement + " OR " + "orders.status LIKE " + queryLikeStatement + " OR orders.date LIKE "
				+ queryLikeStatement + " OR " + "users.firstname LIKE " + queryLikeStatement
				+ " OR users.lastname LIKE " + queryLikeStatement + " OR " + "users.phone LIKE " + queryLikeStatement
				+ " ) GROUP BY orderdetails.orderno "
				+ " ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC ";
		logger.info("Query :- " + query);
		logger.info("Exit at searchTrackDelivery(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}

	public OrderBean getCommentsOfFailedOrder(String orderNumber) {
		logger.info("Entry at getCommentsOfFailedOrder(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.GET_COMMENTS_DETAILS_OF_FAILED_ORDER);
		String status = OrderNaoConstants.ORDER_STATUS_FAILED;
		logger.info("Exit at getCommentsOfFailedOrder(DAO)");
		return jdbcTemplate.queryForObject(sqlQuery, new Object[] { status, orderNumber }, new RowMapper<OrderBean>() {
			@Override
			public OrderBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrderBean orderBean = new OrderBean();

				orderBean.setComments(rs.getString(OrderNaoConstants.COLUMN_NAME_ORDER_COMMENTS));
				orderBean.setCustomerPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_CUSTOMER_PHONE));
				orderBean.setOrderNumber(rs.getInt(OrderNaoConstants.COLUMN_NAME_ORDER_NUMBER));
				orderBean.setOrderDate(rs.getDate(OrderNaoConstants.COLUMN_NAME_ORDER_DATE));
				orderBean.setOrderStatus(rs.getString(OrderNaoConstants.COLUMN_NAME_ORDER_STATUS));
				orderBean.setOrderPickedFrom(rs.getString(OrderNaoConstants.COLUMN_NAME_PICKUP_POINT));
				orderBean.setOrderDeliveredAt(rs.getString(OrderNaoConstants.COLUMN_NAME_DELIVERY_POINT));
				orderBean.setItemName(rs.getString(OrderNaoConstants.COLUMN_NAME_ITEM_NAME));
				orderBean.setServiceCharge(rs.getDouble(OrderNaoConstants.COLUMN_NAME_SERVICE_CHARGE));
				orderBean.setProductCharge(rs.getDouble(OrderNaoConstants.COLUMN_NAME_PRODUCT_CHARGE));
				orderBean.setDeliveryBoyId(rs.getInt(OrderNaoConstants.COLUMN_NAME_USERID));
				orderBean.setDeliveryBoyFirstName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_FIRSTNAME));
				orderBean.setDeliveryBoyLastName(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_LASTNAME));
				orderBean.setDeliveryBoyPhone(rs.getString(OrderNaoConstants.COLUMN_NAME_USER_PHONE));
				return orderBean;
			}
		});

	}

	public int checkForOrderNumberAndFailedStatus(String orderNumber) {
		logger.info("Entry at checkForOrderNumberAndFailedStatus(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.CHECK_FAILED_STATUS);
		logger.info("Query :- " + sqlQuery + "Order Number :- " + orderNumber);
		int result = (Integer) jdbcTemplate.queryForObject(sqlQuery, new Object[] { orderNumber }, Integer.class);
		logger.info("Exit at checkForOrderNumberAndFailedStatus(DAO)");
		return result;
	}

	public int checkForPendingOrDeliveredOrder(int orderNumber) {
		logger.info("Entry at checkForPendingOrFailedOrder(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.CHECK_FOR_PENDING_OR_DELIVERED_ORDER);
		logger.info("Query :- " + sqlQuery + "Order Number :- " + orderNumber);
		int result = (Integer) jdbcTemplate.queryForObject(sqlQuery, new Object[] { orderNumber }, Integer.class);
		logger.info("Exit at checkForPendingOrFailedOrder(DAO)");
		return result;
	}

	public int saveFailedStatusCommentsOfOrder(int orderNumber, String failedComments) {
		logger.info("Entry at saveFailedStatusCommentsOfOrder(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.SAVE_FAILED_COMMENTS_STATUS);
		logger.info("Query :- " + sqlQuery + "Order Number :- " + orderNumber);
		int result = jdbcTemplate.update(sqlQuery, new Object[] { failedComments, orderNumber });
		logger.info("Exit at saveFailedStatusCommentsOfOrder(DAO) Result :- " + result);
		return result;
	}

	public int savePendingOrDeliveredOrderStatus(int orderNumber, String newStatusOfOrder) {
		logger.info("Entry at savePendingOrDeliveredOrderStatus(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.SAVE_PENDING_OR_DELIVERED_ORDER_STATUS);
		logger.info("Query :- " + sqlQuery + "Order Number :- " + orderNumber + " Status :- " + newStatusOfOrder);
		int result = jdbcTemplate.update(sqlQuery, new Object[] { newStatusOfOrder, orderNumber });
		logger.info("Exit at savePendingOrDeliveredOrderStatus(DAO) Result :- " + result);
		return result;
	}

	public int checkDeliveryBoyIdInDB(int deliveryBoyId) {
		logger.info("Entry at checkDeliveryBoyIdInDB(DAO)");
		String sqlQuery = property.getProperty(OrderNaoConstants.CHECK_DELIVERY_BOY_ID_IN_DB);
		logger.info("Query :- " + sqlQuery + "Delivery Boy Id:- " + deliveryBoyId);
		int result = (Integer) jdbcTemplate.queryForObject(sqlQuery, new Object[] { deliveryBoyId }, Integer.class);
		logger.info("Exit at checkDeliveryBoyIdInDB(DAO)");
		return result;
	}

	public List<OrderBean> filterOrdersByStatusAndTime(String date, String status) {
		logger.info("Entry at filterOrdersByStatusAndTime(DAO)");
		StringBuilder query = new StringBuilder(
				"SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,"
						+ "orders.status,orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,"
						+ "orders.totalDistance,orders.productCharge,orders.serviceCharge,users.id,users.firstname,users.lastname,users.phone "
						+ "FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
						+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
						+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE ");
		if (date.equals(OrderNaoConstants.ORDER_DATE_TODAY)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TODAY);
		} else if (date.equals(OrderNaoConstants.ORDER_DATE_LAST_SEVEN_DAYS)) {
			query.append(OrderNaoConstants.MYSQL_DATE_LAST_SEVEN_DAYS);
		} else if (date.equals(OrderNaoConstants.ORDER_DATE_THIS_MONTH)) {
			query.append(OrderNaoConstants.MYSQL_DATE_THIS_MONTH);
		} else if (date.equals(OrderNaoConstants.ORDER_DATE_TILL_DATE)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TILL_DATE);
		}
		query.append(" AND status='" + status
				+ "' GROUP BY orderdetails.orderno ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC");
		logger.info("Query :- " + query.toString());
		logger.info("Entry at filterOrdersByStatusAndTime(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}

	public List<OrderBean> filterOrdersByTimeAndAssignment(String time, String assignment) {
		logger.info("Entry at filterOrdersByTimeAndAssignment(DAO)");
		StringBuilder query = new StringBuilder(
				"SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,"
						+ "orders.status,orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,"
						+ "orders.totalDistance,orders.productCharge,orders.serviceCharge,users.id,users.firstname,users.lastname,users.phone "
						+ "FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
						+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
						+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE ");
		if (time.equals(OrderNaoConstants.ORDER_DATE_TODAY)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TODAY);
		} else if (time.equals(OrderNaoConstants.ORDER_DATE_LAST_SEVEN_DAYS)) {
			query.append(OrderNaoConstants.MYSQL_DATE_LAST_SEVEN_DAYS);
		} else if (time.equals(OrderNaoConstants.ORDER_DATE_THIS_MONTH)) {
			query.append(OrderNaoConstants.MYSQL_DATE_THIS_MONTH);
		} else if (time.equals(OrderNaoConstants.ORDER_DATE_TILL_DATE)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TILL_DATE);
		}

		if (assignment.equals(OrderNaoConstants.ORDER_ASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_AND + OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NOT_NULL);
		} else if (assignment.equals(OrderNaoConstants.ORDER_UNASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_AND + OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NULL);
		}

		query.append(
				" GROUP BY orderdetails.orderno ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC");
		logger.info("Query :- " + query);
		logger.info("Entry at filterOrdersByStatusAndTime(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}

	public List<OrderBean> filterOrdersByStatusAndAssignment(String status, String assignment) {
		logger.info("Entry at filterOrdersByStatusAndAssignment(DAO)");
		StringBuilder query = new StringBuilder(
				"SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,"
						+ "orders.status,orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,"
						+ "orders.totalDistance,orders.serviceCharge,orders.productCharge,users.id,users.firstname,users.lastname,users.phone "
						+ "FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
						+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
						+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE ");
		if (assignment.equals(OrderNaoConstants.ORDER_ASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NOT_NULL);
		} else if (assignment.equals(OrderNaoConstants.ORDER_UNASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NULL);
		}
		query.append(" AND orders.status='" + status
				+ "' GROUP BY orderdetails.orderno ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC");
		logger.info("Query :- " + query.toString());
		logger.info("Exit at filterOrdersByStatusAndAssignment(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}

	public List<OrderBean> filterOrdersByStatusAndTimeAndAssignment(String status, String time, String assignment) {
		logger.info("Entry at filterOrdersByStatusAndTimeAndAssignment(DAO)");
		StringBuilder query = new StringBuilder(
				"SELECT customer.phone,COUNT(*) AS orderCount,orderdetails.orderno,orders.date,"
						+ "orders.status,orderdetails.pickuppoint,orderdetails.deliverypoint,orderdetails.itemName,"
						+ "orders.totalDistance,orders.productCharge,orders.serviceCharge,users.id,users.firstname,users.lastname,users.phone "
						+ "FROM orders " + "INNER JOIN customer ON customer.id=orders.clientid "
						+ "INNER JOIN orderdetails ON orders.orderno=orderdetails.orderno "
						+ "LEFT JOIN users ON users.id=orders.DeliveryPersonId " + "WHERE ");
		if (time.equals(OrderNaoConstants.ORDER_DATE_TODAY)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TODAY);
		} else if (time.equals(OrderNaoConstants.ORDER_DATE_LAST_SEVEN_DAYS)) {
			query.append(OrderNaoConstants.MYSQL_DATE_LAST_SEVEN_DAYS);
		} else if (time.equals(OrderNaoConstants.ORDER_DATE_THIS_MONTH)) {
			query.append(OrderNaoConstants.MYSQL_DATE_THIS_MONTH);
		} else if (time.equals(OrderNaoConstants.ORDER_DATE_TILL_DATE)) {
			query.append(OrderNaoConstants.MYSQL_DATE_TILL_DATE);
		}

		if (assignment.equals(OrderNaoConstants.ORDER_ASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_AND + OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NOT_NULL);
		} else if (assignment.equals(OrderNaoConstants.ORDER_UNASSIGNED)) {
			query.append(OrderNaoConstants.MYSQL_AND + OrderNaoConstants.MYSQL_DELIVERY_BOY_ID_NULL);
		}

		query.append("AND orders.status='" + status
				+ "' GROUP BY orderdetails.orderno ORDER BY FIELD(status, 'Pending','Delivered','Failed'),orders.date ASC");
		logger.info("Query :- " + query);
		logger.info("Entry at filterOrdersByStatusAndTimeAndAssignment(DAO)");
		return jdbcTemplate.query(query.toString(), new TrackOrderRowMapper());
	}
}
