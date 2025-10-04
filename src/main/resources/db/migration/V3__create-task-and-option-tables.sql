CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_type VARCHAR(31) NOT NULL,
    statement VARCHAR(255) NOT NULL,
    task_order INT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_task_course FOREIGN KEY (course_id) REFERENCES Course(id),
    UNIQUE (course_id, statement)
);

CREATE TABLE options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_text VARCHAR(80) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    task_id BIGINT NOT NULL,
    CONSTRAINT fk_option_task FOREIGN KEY (task_id) REFERENCES tasks(id)
);