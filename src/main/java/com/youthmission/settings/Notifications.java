package com.youthmission.settings;

import com.youthmission.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notifications {

    private boolean umionCreatedByEmail;//유미온 생성되면 이메일로 알림을 받을 것인가?

    private boolean umionCreatedByWeb;

    private boolean umionEnrollmentResultByEmail;//유미온 가입 결과를 이메일로 받을 것인가?

    private boolean umionEnrollmentResultByWeb;

    private boolean umionUpdatedByEmail; //유미온 내용 변경을 이메일로 받을 것인가?

    private boolean umionUpdatedByWeb;

}
