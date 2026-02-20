package com.vaadin.tutorial.crm.utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataUtility {
    @Autowired
    private JdbcTemplate template;

    public String getCurrentDatabase() {
        String sql = "SELECT PARAM_VALUE FROM SYS_PARAM_VALUES WHERE PARAM_KEY = 'DATABASE_NAME'";
        String list = (String)template.queryForObject(sql, String.class);
        return list;
    }

    public List<String> getCurrentUserRole(String userName) {
        String sql = "select cat.name from CNU_USER usr inner join USER_USERCATEGORY usrcat on usrcat.fk_user=usr.object_id inner join USER_CATEGORY cat on cat.object_id=usrcat.fk_usercategory where lower(usr.user_id)=lower('"+ userName +"') and cat.name in ('RM_SUPERUSER', 'RM_USER')";
        List<String> list=template.queryForList(sql, String.class);
        return list;
    }

    public boolean hasRole (String roleName)
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }

    public String getDocumentPath(Long recordId) {
        String list="";
        String sql = "select document_path from rm_report_attributes where id="+Long.toString(recordId);
        try {
            list = (String)template.queryForObject(sql, String.class);
        } catch (Exception e) {
            list="";
        }
        return list;
    }

    public String getDocumentFormat(Long recordId) {
        String list="";
        String sql = "select document_format from rm_report_attributes where id="+Long.toString(recordId);
        try {
            list = (String)template.queryForObject(sql, String.class);
        } catch (Exception e) {
            list="";
        }
        return list;
    }
    public String getDocumentName(Long recordId) {
        String list="";
        String sql = "select document_name from rm_report_attributes where id="+Long.toString(recordId);
        try {
            list = (String)template.queryForObject(sql, String.class);
        } catch (Exception e) {
            list="";
        }
        return list;
    }
}
