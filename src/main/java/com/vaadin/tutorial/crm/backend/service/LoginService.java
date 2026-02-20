package com.vaadin.tutorial.crm.backend.service;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.sicsnt.systemtypes.AuthenticationToken;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import com.vaadin.tutorial.crm.utility.DataUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.sicsnt.administration.LoginInput;
// import com.sicsnt.systemtypes.AuthenticationToken;
// import com.sicswsadministrationentrypoint.sicswsadministrationentrypoint.FaultDetail;
// import com.sicswsadministrationentrypoint.sicswsadministrationentrypoint.SicsWsAdministrationEntryPoint;
// import com.sicswsadministrationentrypoint.sicswsadministrationentrypoint.SicsWsAdministrationEntryPointPort;

@Component
public class LoginService implements AuthenticationProvider
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProvider.class);

    @Value("${sicsserver.instance.url}")
    private String url;


    @Value("${ldap.url}")
    private String ldapURL;

    @Value("${ldap.domain}")
    private String ldapDomain;

    public AuthenticationToken authenticationToken;

    // private SicsWsAdministrationEntryPointPort sicsWsAdministrationEntryPointPort;

    private AuditService auditService;
    private Audit audit;
    private DataUtility dataUtility;

    @Autowired
    public void configure(AuditService auditService, DataUtility dataUtility) {
        this.auditService = auditService;
        this.dataUtility = dataUtility;
    }

    @PostConstruct
    private void init()
            throws Exception
    {
        // URL sicsWsAdministrationEntryPointUrl = new URL(this.url + "/GenerateJAXWSClient/sicsxml/SicsWsAdministrationEntryPoint.wsdl");
        // SicsWsAdministrationEntryPoint sicsWsAdministrationEntryPoint = new SicsWsAdministrationEntryPoint(sicsWsAdministrationEntryPointUrl);
        // this.sicsWsAdministrationEntryPointPort = sicsWsAdministrationEntryPoint.getSicsWsAdministrationEntryPointPort();
    }

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException
    {
        try
        {
            LoginInput loginInput = new LoginInput();
            loginInput.setUserId(authentication.getName());
            loginInput.setPassword(authentication.getCredentials() != null ? authentication.getCredentials().toString() : null);

            Boolean isAuthenticate = authenticateLdap(loginInput.getUserId(), loginInput.getPassword());

            if (isAuthenticate) {
                Collection<GrantedAuthority> grantedAuthorities = new ArrayList();
                //grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
                List<String> userRole=dataUtility.getCurrentUserRole(authentication.getName());
                userRole.forEach(name ->{
                    grantedAuthorities.add(new SimpleGrantedAuthority(name));
                });

                logger.info("Authenticate Name       : " + authentication.getName());
                logger.info("Authenticate Credentials: " + authentication.getCredentials());

                // System.out.println("Authenticate Name       : " + authentication.getName());
                // System.out.println("Authenticate Credentials: " + authentication.getCredentials());

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials(), grantedAuthorities);
                usernamePasswordAuthenticationToken.setDetails(authenticationToken);

                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
                Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                audit=new Audit();
                audit.setOperation("Login");
                audit.setEntry_date(currentTimestamp);
                audit.setUser_name(authentication.getName());
                auditService.save(audit);

                return usernamePasswordAuthenticationToken;

            } else {

                return null;
            }

        }
        catch (NamingException e)
        {
            System.err.println("UsernameNotFoundException :" + e.getMessage());
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    /*
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException
    {
        try
        {
            LoginInput loginInput = new LoginInput();
            loginInput.setUserId(authentication.getName());
            loginInput.setPassword(authentication.getCredentials() != null ? authentication.getCredentials().toString() : null);
            authenticationToken = this.sicsWsAdministrationEntryPointPort.login(loginInput);

            Collection<GrantedAuthority> grantedAuthorities = new ArrayList();
            List<String> userRole=dataUtility.getCurrentUserRole(authentication.getName());
            userRole.forEach(name ->{
                grantedAuthorities.add(new SimpleGrantedAuthority(name));
            });

            logger.info("Authenticate Name       : " + authentication.getName());
            logger.info("Authenticate Credentials: " + authentication.getCredentials());

            System.out.println("Authenticate Name       : " + authentication.getName());
            System.out.println("Authenticate Credentials: " + authentication.getCredentials());

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials(), grantedAuthorities);
            usernamePasswordAuthenticationToken.setDetails(authenticationToken);

            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

            audit=new Audit();
            audit.setOperation("Login");
            audit.setEntry_date(currentTimestamp);
            audit.setUser_name(authentication.getName());
            auditService.save(audit);

            return usernamePasswordAuthenticationToken;
        }
        catch (FaultDetail e)
        {
            System.err.println("UsernameNotFoundException :" + e.getFaultInfo().getExplanation());
            throw new UsernameNotFoundException(e.getFaultInfo().getExplanation(), e);
        }
    }
    */

    public boolean supports(Class<?> authentication)
    {
        //return authentication.equals(UsernamePasswordAuthenticationToken.class);
        return true;
    }


    public Boolean authenticateLdap(final String username, final String password) throws NamingException {
        try {

            DirContext ctx = null;
            Hashtable<String, String> ldapEnvironment = new Hashtable<>();

            ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            ldapEnvironment.put(Context.PROVIDER_URL, this.ldapURL);
            ldapEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");

            ldapEnvironment.put(Context.SECURITY_PRINCIPAL, username + "@" + this.ldapDomain);
            ldapEnvironment.put(Context.SECURITY_CREDENTIALS, password);


            ctx = new InitialDirContext(ldapEnvironment);

            ctx.close();
            return true;

        } catch (javax.naming.NamingException e) {

            return false;
        }
    }


}
