package org.jasig.cas.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * Controller to delete ticket granting ticket cookie in order to log out of
 * single sign on. This controller implements the idea of the ESUP Portail's
 * Logout patch to allow for redirecting to a url on logout. It also exposes a
 * log out link to the view via the WebConstants.LOGOUT constant.
 * 
 * @author Scott Battaglia
 * @version $Revision: 19533 $ $Date: 2009-12-14 23:33:36 -0500 (Mon, 14 Dec 2009) $
 * @since 3.0
 */
public final class LogoutController extends AbstractController {

    /** The CORE to which we delegate for all CAS functionality. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    /** CookieGenerator for TGT Cookie */
    @NotNull
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    /** CookieGenerator for Warn Cookie */
    @NotNull
    private CookieRetrievingCookieGenerator warnCookieGenerator;

    /** Logout view name. */
    @NotNull
    private String logoutView;

    /**
     * Boolean to determine if we will redirect to any url provided in the
     * service request parameter.
     */
    private boolean followServiceRedirects;
    
    public LogoutController() {
        setCacheSeconds(0);
    }

    protected ModelAndView handleRequestInternal(
        final HttpServletRequest request, final HttpServletResponse response)
        throws Exception {
        final String ticketGrantingTicketId = this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        final String service = request.getParameter("service");

        if (ticketGrantingTicketId != null) {
            this.centralAuthenticationService
                .destroyTicketGrantingTicket(ticketGrantingTicketId);

            this.ticketGrantingTicketCookieGenerator.removeCookie(response);
            this.warnCookieGenerator.removeCookie(response);
        }

        if (this.followServiceRedirects && service != null) {
            //return new ModelAndView(new RedirectView(service));
        	request.setAttribute("service", service);
            return new ModelAndView(this.logoutView);
        }

        return new ModelAndView(this.logoutView);
    }

    public void setTicketGrantingTicketCookieGenerator(
        final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator) {
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
    }

    public void setWarnCookieGenerator(final CookieRetrievingCookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }

    /**
     * @param centralAuthenticationService The centralAuthenticationService to
     * set.
     */
    public void setCentralAuthenticationService(
        final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setFollowServiceRedirects(final boolean followServiceRedirects) {
        this.followServiceRedirects = followServiceRedirects;
    }

    public void setLogoutView(final String logoutView) {
        this.logoutView = logoutView;
    }
}