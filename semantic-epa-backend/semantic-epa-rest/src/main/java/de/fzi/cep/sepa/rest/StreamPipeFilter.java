package de.fzi.cep.sepa.rest;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Requires the requesting user to be authenticated for the request to continue and if they are not,
 * responds with HTML Error code 403.
 *
 * See https://stackoverflow.com/questions/30344441/shiro-filter-without-redirect/30344953#30344953
 * Created by robin on 21.05.15.
 */

public class StreamPipeFilter extends FormAuthenticationFilter{
    private static final String message = "Access denied!";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse ;
        try { httpResponse = WebUtils.toHttp(response); }

        catch (ClassCastException ex) {
            // Not a HTTP Servlet operation
            return super.onAccessDenied(request, response) ;
        }
        if ( message == null )
            httpResponse.sendError(403) ;
        else
            httpResponse.sendError(403, message) ;
        return false ;
    }
}
