package org.velvet;

import org.velvet.model.controller.AdminController;
import org.velvet.model.controller.CustomerController;
import org.velvet.model.controller.LoginController;
import org.velvet.model.controller.ManagerController;
import org.velvet.model.controller.SchedulerController;
import org.velvet.model.service.BookingService;
import org.velvet.model.service.HallService;
import org.velvet.model.service.IssueService;
import org.velvet.model.service.PaymentService;
import org.velvet.model.service.ReportService;
import org.velvet.model.service.UserService;

public final class AppContext {
    public static final UserService USER_SERVICE = new UserService();
    public static final HallService HALL_SERVICE = new HallService();
    public static final BookingService BOOKING_SERVICE = new BookingService();
    public static final PaymentService PAYMENT_SERVICE = new PaymentService();
    public static final IssueService ISSUE_SERVICE = new IssueService();
    public static final ReportService REPORT_SERVICE = new ReportService(BOOKING_SERVICE);

    public static final LoginController LOGIN_CONTROLLER = new LoginController(USER_SERVICE);
    public static final CustomerController CUSTOMER_CONTROLLER = new CustomerController(
            USER_SERVICE,
            HALL_SERVICE,
            BOOKING_SERVICE,
            PAYMENT_SERVICE,
            ISSUE_SERVICE
    );
    public static final SchedulerController SCHEDULER_CONTROLLER = new SchedulerController(HALL_SERVICE, BOOKING_SERVICE);
    public static final AdminController ADMIN_CONTROLLER = new AdminController(USER_SERVICE, BOOKING_SERVICE);
    public static final ManagerController MANAGER_CONTROLLER = new ManagerController(REPORT_SERVICE, ISSUE_SERVICE, USER_SERVICE);

    private AppContext() {
    }
}
