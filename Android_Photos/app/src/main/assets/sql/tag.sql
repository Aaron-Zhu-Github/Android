create table tb_tag(
    tag_no        varchar(20),
    tag_name      varchar(200),
    file_name     varchar(200),
    file_path     varchar(200),
    location      varchar(200),
    constraint tb_tag_primary primary key(
        tag_no
    )
);