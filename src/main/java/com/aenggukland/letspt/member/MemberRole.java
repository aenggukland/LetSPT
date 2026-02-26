package com.aenggukland.letspt.member;

public enum MemberRole {
    MEMBER(1L),
    TRAINER(2L),
    MASTER(3L);

    private final long roleId;

    MemberRole(long roleId) {
        this.roleId = roleId;
    }

    public long getRoleId() {
        return roleId;
    }

    public static MemberRole fromRoleId(long roleId) {
        for (MemberRole role : values()) {
            if (role.roleId == roleId) return role;
        }
        throw new IllegalArgumentException("알 수 없는 roleId: " + roleId);
    }
}
