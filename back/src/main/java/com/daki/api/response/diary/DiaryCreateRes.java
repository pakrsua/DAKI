package com.daki.api.response.diary;

import com.daki.db.entity.Diary;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ApiModel

public class DiaryCreateRes {

    private Long diaryNo;

    private String diaryTitle;

    private String diaryContent;

    private LocalDateTime diaryDate;

    public DiaryCreateRes(Diary diary) {
        this.diaryNo = diary.getDiaryNo();
        this.diaryTitle = diary.getDiaryTitle();
        this.diaryContent = diary.getDiaryContent();
        this.diaryDate = diary.getDiaryDate();
    }

}
