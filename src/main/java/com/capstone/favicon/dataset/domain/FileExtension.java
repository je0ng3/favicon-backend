package com.capstone.favicon.dataset.domain;

public enum FileExtension {

    // DB의 enum과 대소문자 통일해야함
    // DB에 csv면 CSV("csv")는 에러 발생
    csv("csv"),
    xlsx("xlsx"),
    json("json"),
    txt("txt");

    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

}
