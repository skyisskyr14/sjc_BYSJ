package com.sq.system.security.resolver;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.sq.system.security.model.SjcUserRoleInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

@Component
public class SjcRoleResolver {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @DS("zxq")
    public SjcUserRoleInfo resolveByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        String sql = """
                SELECT r.id AS role_id, r.role_code, r.role_name
                FROM sjc_user_role ur
                LEFT JOIN sjc_role r ON r.id = ur.role_id
                WHERE ur.sys_user_id = ?
                  AND ur.is_delete = 0
                ORDER BY ur.id DESC
                LIMIT 1
                """;
        List<SjcUserRoleInfo> rows = jdbcTemplate.query(sql, (rs, i) ->
                new SjcUserRoleInfo(
                        rs.getLong("role_id"),
                        rs.getString("role_code"),
                        rs.getString("role_name")
                ), userId);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
