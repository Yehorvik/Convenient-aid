package ua.edu.sumdu.volonteerProject.security;

public class SecurityConstraints {
    public static final String LOGIN_URL = "/api/user/**";
    public static final String ADMIN_URL = "/admin/**";
    public static final String SECRET = "6!;wz]i7{.U/tdBN}nX(atpRzHMiTmL/a?P!n5NKr[uWA4-Jn$c::9TvW}y!VFi;r&;!m,W9d_pn}-*D:6-b{;,ZjPh7%Db*v]B@";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long TOKEN_EXPIRATION_TIME = 30000;
}
