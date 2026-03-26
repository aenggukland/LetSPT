package com.aenggukland.letspt.member;

// 회원 역할 Enum: DB에는 role_id(Long)로 저장된다
// MEMBER(일반 회원), TRAINER(트레이너), MASTER(관리자)
public enum MemberRole {
    MEMBER(1L),
    TRAINER(2L),
    MASTER(3L);

    private final long roleId;

    MemberRole(long roleId) {
        this.roleId = roleId;
    }

    // DB에 저장된 role_id 숫자 값을 반환한다
    public long getRoleId() {
        return roleId;
    }

    // role_id 숫자로 MemberRole Enum을 반환한다
    // 매핑되는 값이 없으면 IllegalArgumentException 발생 (호출부에서 처리 필요, TODO B8)
    public static MemberRole fromRoleId(long roleId) {
        for (MemberRole role : values()) {
            if (role.roleId == roleId) return role;
        }
        throw new IllegalArgumentException("알 수 없는 roleId: " + roleId);
    }
}
