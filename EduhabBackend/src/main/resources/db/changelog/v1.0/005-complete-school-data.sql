BEGIN;

DELETE FROM homework_completions;
DELETE FROM announcement_reads;
DELETE FROM notifications;
DELETE FROM attendance;
DELETE FROM grades;
DELETE FROM assignments;
DELETE FROM schedule_breaks;
DELETE FROM lessons;
ALTER SEQUENCE IF EXISTS lessons_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS schedule_breaks_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS grades_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS attendance_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS assignments_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS homework_completions_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS notifications_id_seq RESTART WITH 1;

INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 1, 1, 1, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 3, 3, 1, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 7, 6, 1, '10:25:00', '11:10:00', '301', 'PRACTICAL', 1, FALSE, 'Passive Voice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 13, 11, 1, '11:25:00', '12:10:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Бasketball');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 9, 8, 1, '12:20:00', '13:05:00', '206', 'LECTURE', 1, FALSE, 'Древняя Русь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 2, 2, 2, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Треугольники');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 5, 4, 2, '09:25:00', '10:10:00', '204', 'LECTURE', 1, FALSE, 'Электричество');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 4, 3, 2, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Поэзия Серебряного века');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 10, 9, 2, '11:25:00', '12:10:00', '205', 'PRACTICAL', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 6, 5, 2, '12:20:00', '13:05:00', '207', 'PRACTICAL', 1, FALSE, 'Алгоритмы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 1, 1, 3, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 3, 3, 3, '09:25:00', '10:10:00', '105', 'PRACTICAL', 1, FALSE, 'Сочинение-описание');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 12, 10, 3, '10:25:00', '11:10:00', '209', 'LECTURE', 1, FALSE, 'Климат');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 7, 6, 3, '11:25:00', '12:10:00', '301', 'LECTURE', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 14, 11, 3, '12:20:00', '13:05:00', '101', 'LECTURE', 1, FALSE, 'Первая помощь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 5, 4, 4, '08:30:00', '09:15:00', '204', 'PRACTICAL', 1, FALSE, 'Давление');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 4, 3, 4, '09:25:00', '10:10:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 10, 9, 4, '10:25:00', '11:10:00', '205', 'LECTURE', 1, FALSE, 'Генетика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 9, 8, 4, '11:25:00', '12:10:00', '206', 'SEMINAR', 1, FALSE, 'XIX век');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 3, 3, 4, '12:20:00', '13:05:00', '105', 'LECTURE', 1, FALSE, 'Сочинение-описание');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 2, 2, 5, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Параллелограммы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 6, 5, 5, '09:25:00', '10:10:00', '207', 'LECTURE', 1, FALSE, 'Алгоритмы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 1, 1, 5, '10:25:00', '11:10:00', '201', 'LECTURE', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 7, 6, 5, '11:25:00', '12:10:00', '301', 'PRACTICAL', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (1, 13, 11, 5, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Лёгкая атлетика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 1, 1, 1, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 3, 3, 1, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Имя существительное');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 8, 7, 1, '10:25:00', '11:10:00', '208', 'LECTURE', 1, FALSE, 'ОВР');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 7, 6, 1, '11:25:00', '12:10:00', '301', 'PRACTICAL', 1, FALSE, 'Passive Voice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 9, 8, 1, '12:20:00', '13:05:00', '206', 'LECTURE', 1, FALSE, 'XIX век');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 2, 2, 2, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Треугольники');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 5, 4, 2, '09:25:00', '10:10:00', '204', 'PRACTICAL', 1, FALSE, 'Электричество');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 4, 3, 2, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 10, 9, 2, '11:25:00', '12:10:00', '205', 'PRACTICAL', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 6, 5, 2, '12:20:00', '13:05:00', '207', 'PRACTICAL', 1, FALSE, 'Алгоритмы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 1, 1, 3, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 8, 7, 3, '09:25:00', '10:10:00', '208', 'PRACTICAL', 1, FALSE, 'Органическая химия');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 3, 3, 3, '10:25:00', '11:10:00', '105', 'PRACTICAL', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 12, 10, 3, '11:25:00', '12:10:00', '209', 'LECTURE', 1, FALSE, 'Рельеф');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 13, 11, 3, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Волейбол');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 5, 4, 4, '08:30:00', '09:15:00', '204', 'LECTURE', 1, FALSE, 'Механика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 4, 3, 4, '09:25:00', '10:10:00', '105', 'SEMINAR', 1, FALSE, 'Лермонтов «Мцыри»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 10, 9, 4, '10:25:00', '11:10:00', '205', 'LECTURE', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 9, 8, 4, '11:25:00', '12:10:00', '206', 'SEMINAR', 1, FALSE, 'XIX век');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 7, 6, 4, '12:20:00', '13:05:00', '301', 'LECTURE', 1, FALSE, 'Present Perfect');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 2, 2, 5, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Окружность');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 6, 5, 5, '09:25:00', '10:10:00', '207', 'LECTURE', 1, FALSE, 'Базы данных');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 1, 1, 5, '10:25:00', '11:10:00', '201', 'LECTURE', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 8, 7, 5, '11:25:00', '12:10:00', '208', 'LECTURE', 1, FALSE, 'ОВР');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (6, 14, 11, 5, '12:20:00', '13:05:00', '101', 'LECTURE', 1, FALSE, 'Безопасность дома');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 3, 3, 1, '08:30:00', '09:15:00', '105', 'LECTURE', 1, FALSE, 'Имя существительное');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 1, 1, 1, '09:25:00', '10:10:00', '201', 'LECTURE', 1, FALSE, 'Степень с натуральным показателем');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 7, 6, 1, '10:25:00', '11:10:00', '301', 'PRACTICAL', 1, FALSE, 'Present Perfect');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 10, 9, 1, '11:25:00', '12:10:00', '205', 'LECTURE', 1, FALSE, 'Генетика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 13, 11, 1, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Бasketball');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 2, 2, 2, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Параллелограммы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 8, 7, 2, '09:25:00', '10:10:00', '208', 'LECTURE', 1, FALSE, 'ОВР');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 4, 3, 2, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 5, 4, 2, '11:25:00', '12:10:00', '204', 'PRACTICAL', 1, FALSE, 'Механика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 6, 5, 2, '12:20:00', '13:05:00', '207', 'PRACTICAL', 1, FALSE, 'Алгоритмы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 1, 1, 3, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 3, 3, 3, '09:25:00', '10:10:00', '105', 'PRACTICAL', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 12, 10, 3, '10:25:00', '11:10:00', '209', 'LECTURE', 1, FALSE, 'Природные зоны');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 8, 7, 3, '11:25:00', '12:10:00', '208', 'PRACTICAL', 1, FALSE, 'Кислоты и основания');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 9, 8, 3, '12:20:00', '13:05:00', '206', 'LECTURE', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 5, 4, 4, '08:30:00', '09:15:00', '204', 'LECTURE', 1, FALSE, 'Электричество');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 4, 3, 4, '09:25:00', '10:10:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 10, 9, 4, '10:25:00', '11:10:00', '205', 'PRACTICAL', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 7, 6, 4, '11:25:00', '12:10:00', '301', 'LECTURE', 1, FALSE, 'Passive Voice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 3, 3, 4, '12:20:00', '13:05:00', '105', 'LECTURE', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 2, 2, 5, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Окружность');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 6, 5, 5, '09:25:00', '10:10:00', '207', 'LECTURE', 1, FALSE, 'Python: циклы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 1, 1, 5, '10:25:00', '11:10:00', '201', 'LECTURE', 1, FALSE, 'Степень с натуральным показателем');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 13, 11, 5, '11:25:00', '12:10:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Лёгкая атлетика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (7, 14, 11, 5, '12:20:00', '13:05:00', '101', 'LECTURE', 1, FALSE, 'Первая помощь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 1, 1, 1, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 5, 4, 1, '09:25:00', '10:10:00', '204', 'LECTURE', 1, FALSE, 'Электричество');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 3, 3, 1, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Имя существительное');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 8, 7, 1, '11:25:00', '12:10:00', '208', 'PRACTICAL', 1, FALSE, 'Органическая химия');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 7, 6, 1, '12:20:00', '13:05:00', '301', 'PRACTICAL', 1, FALSE, 'Passive Voice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 2, 2, 2, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Окружность');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 5, 4, 2, '09:25:00', '10:10:00', '204', 'PRACTICAL', 1, FALSE, 'Механика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 6, 5, 2, '10:25:00', '11:10:00', '207', 'PRACTICAL', 1, FALSE, 'Алгоритмы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 4, 3, 2, '11:25:00', '12:10:00', '105', 'LECTURE', 1, FALSE, 'Поэзия Серебряного века');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 1, 1, 2, '12:20:00', '13:05:00', '201', 'PRACTICAL', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 3, 3, 3, '08:30:00', '09:15:00', '105', 'PRACTICAL', 1, FALSE, 'Сочинение-описание');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 8, 7, 3, '09:25:00', '10:10:00', '208', 'LECTURE', 1, FALSE, 'Кислоты и основания');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 2, 2, 3, '10:25:00', '11:10:00', '201', 'LECTURE', 1, FALSE, 'Треугольники');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 10, 9, 3, '11:25:00', '12:10:00', '205', 'LECTURE', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 14, 11, 3, '12:20:00', '13:05:00', '101', 'LECTURE', 1, FALSE, 'Безопасность дома');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 1, 1, 4, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 5, 4, 4, '09:25:00', '10:10:00', '204', 'LECTURE', 1, FALSE, 'Механика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 6, 5, 4, '10:25:00', '11:10:00', '207', 'LECTURE', 1, FALSE, 'Алгоритмы');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 9, 8, 4, '11:25:00', '12:10:00', '206', 'SEMINAR', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 2, 2, 4, '12:20:00', '13:05:00', '201', 'PRACTICAL', 1, FALSE, 'Окружность');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 3, 3, 5, '08:30:00', '09:15:00', '105', 'LECTURE', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 4, 3, 5, '09:25:00', '10:10:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 8, 7, 5, '10:25:00', '11:10:00', '208', 'PRACTICAL', 1, FALSE, 'Кислоты и основания');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 7, 6, 5, '11:25:00', '12:10:00', '301', 'LECTURE', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (2, 13, 11, 5, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Бasketball');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 1, 1, 1, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 5, 4, 1, '09:25:00', '10:10:00', '204', 'LECTURE', 1, FALSE, 'Давление');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 3, 3, 1, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Имя существительное');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 8, 7, 1, '11:25:00', '12:10:00', '208', 'PRACTICAL', 1, FALSE, 'ОВР');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 10, 9, 1, '12:20:00', '13:05:00', '205', 'LECTURE', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 2, 2, 2, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Треугольники');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 5, 4, 2, '09:25:00', '10:10:00', '204', 'PRACTICAL', 1, FALSE, 'Электричество');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 6, 5, 2, '10:25:00', '11:10:00', '207', 'PRACTICAL', 1, FALSE, 'Базы данных');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 4, 3, 2, '11:25:00', '12:10:00', '105', 'LECTURE', 1, FALSE, 'Лермонтов «Мцыри»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 8, 7, 2, '12:20:00', '13:05:00', '208', 'LECTURE', 1, FALSE, 'Органическая химия');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 1, 1, 3, '08:30:00', '09:15:00', '201', 'PRACTICAL', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 10, 9, 3, '09:25:00', '10:10:00', '205', 'PRACTICAL', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 3, 3, 3, '10:25:00', '11:10:00', '105', 'PRACTICAL', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 7, 6, 3, '11:25:00', '12:10:00', '301', 'LECTURE', 1, FALSE, 'Present Perfect');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 9, 8, 3, '12:20:00', '13:05:00', '206', 'SEMINAR', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 5, 4, 4, '08:30:00', '09:15:00', '204', 'LECTURE', 1, FALSE, 'Электричество');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 8, 7, 4, '09:25:00', '10:10:00', '208', 'PRACTICAL', 1, FALSE, 'Кислоты и основания');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 6, 5, 4, '10:25:00', '11:10:00', '207', 'LECTURE', 1, FALSE, 'Базы данных');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 2, 2, 4, '11:25:00', '12:10:00', '201', 'LECTURE', 1, FALSE, 'Треугольники');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 4, 3, 4, '12:20:00', '13:05:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 1, 1, 5, '08:30:00', '09:15:00', '201', 'LECTURE', 1, FALSE, 'Системы уравнений');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 10, 9, 5, '09:25:00', '10:10:00', '205', 'LECTURE', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 3, 3, 5, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Сочинение-описание');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 7, 6, 5, '11:25:00', '12:10:00', '301', 'PRACTICAL', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (4, 13, 11, 5, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Бasketball');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 3, 3, 1, '08:30:00', '09:15:00', '105', 'LECTURE', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 4, 3, 1, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Поэзия Серебряного века');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 9, 8, 1, '10:25:00', '11:10:00', '206', 'LECTURE', 1, FALSE, 'Древняя Русь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 11, 8, 1, '11:25:00', '12:10:00', '206', 'SEMINAR', 1, FALSE, 'Конституция РФ');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 7, 6, 1, '12:20:00', '13:05:00', '301', 'PRACTICAL', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 4, 3, 2, '08:30:00', '09:15:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 9, 8, 2, '09:25:00', '10:10:00', '206', 'SEMINAR', 1, FALSE, 'Древняя Русь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 12, 10, 2, '10:25:00', '11:10:00', '209', 'LECTURE', 1, FALSE, 'Природные зоны');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 3, 3, 2, '11:25:00', '12:10:00', '105', 'PRACTICAL', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 1, 1, 2, '12:20:00', '13:05:00', '201', 'LECTURE', 1, FALSE, 'Степень с натуральным показателем');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 11, 8, 3, '08:30:00', '09:15:00', '206', 'LECTURE', 1, FALSE, 'Экономика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 4, 3, 3, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Поэзия Серебряного века');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 7, 6, 3, '10:25:00', '11:10:00', '301', 'LECTURE', 1, FALSE, 'Present Perfect');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 9, 8, 3, '11:25:00', '12:10:00', '206', 'LECTURE', 1, FALSE, 'Древняя Русь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 2, 2, 3, '12:20:00', '13:05:00', '201', 'LECTURE', 1, FALSE, 'Окружность');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 3, 3, 4, '08:30:00', '09:15:00', '105', 'PRACTICAL', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 4, 3, 4, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 1, 1, 4, '10:25:00', '11:10:00', '201', 'PRACTICAL', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 12, 10, 4, '11:25:00', '12:10:00', '209', 'SEMINAR', 1, FALSE, 'Рельеф');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 10, 9, 4, '12:20:00', '13:05:00', '205', 'LECTURE', 1, FALSE, 'Экология');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 9, 8, 5, '08:30:00', '09:15:00', '206', 'LECTURE', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 11, 8, 5, '09:25:00', '10:10:00', '206', 'SEMINAR', 1, FALSE, 'Экономика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 7, 6, 5, '10:25:00', '11:10:00', '301', 'PRACTICAL', 1, FALSE, 'Passive Voice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 4, 3, 5, '11:25:00', '12:10:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (5, 13, 11, 5, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Бasketball');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 4, 3, 1, '08:30:00', '09:15:00', '105', 'LECTURE', 1, FALSE, 'Лермонтов «Мцыри»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 9, 8, 1, '09:25:00', '10:10:00', '206', 'LECTURE', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 3, 3, 1, '10:25:00', '11:10:00', '105', 'LECTURE', 1, FALSE, 'Имя существительное');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 11, 8, 1, '11:25:00', '12:10:00', '206', 'SEMINAR', 1, FALSE, 'Право');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 7, 6, 1, '12:20:00', '13:05:00', '301', 'PRACTICAL', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 4, 3, 2, '08:30:00', '09:15:00', '105', 'SEMINAR', 1, FALSE, 'Гоголь «Шинель»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 9, 8, 2, '09:25:00', '10:10:00', '206', 'SEMINAR', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 1, 1, 2, '10:25:00', '11:10:00', '201', 'LECTURE', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 3, 3, 2, '11:25:00', '12:10:00', '105', 'PRACTICAL', 1, FALSE, 'Сочинение-описание');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 12, 10, 2, '12:20:00', '13:05:00', '209', 'LECTURE', 1, FALSE, 'Природные зоны');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 11, 8, 3, '08:30:00', '09:15:00', '206', 'LECTURE', 1, FALSE, 'Экономика');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 4, 3, 3, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Лермонтов «Мцыри»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 7, 6, 3, '10:25:00', '11:10:00', '301', 'LECTURE', 1, FALSE, 'Reading practice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 9, 8, 3, '11:25:00', '12:10:00', '206', 'LECTURE', 1, FALSE, 'Древняя Русь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 10, 9, 3, '12:20:00', '13:05:00', '205', 'LECTURE', 1, FALSE, 'Клетка');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 3, 3, 4, '08:30:00', '09:15:00', '105', 'PRACTICAL', 1, FALSE, 'Причастие');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 4, 3, 4, '09:25:00', '10:10:00', '105', 'LECTURE', 1, FALSE, 'Лермонтов «Мцыри»');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 1, 1, 4, '10:25:00', '11:10:00', '201', 'PRACTICAL', 1, FALSE, 'Линейные уравнения');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 12, 10, 4, '11:25:00', '12:10:00', '209', 'SEMINAR', 1, FALSE, 'Рельеф');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 14, 11, 4, '12:20:00', '13:05:00', '101', 'LECTURE', 1, FALSE, 'Первая помощь');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 9, 8, 5, '08:30:00', '09:15:00', '206', 'LECTURE', 1, FALSE, 'Великая Отечественная война');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 11, 8, 5, '09:25:00', '10:10:00', '206', 'SEMINAR', 1, FALSE, 'Право');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 7, 6, 5, '10:25:00', '11:10:00', '301', 'PRACTICAL', 1, FALSE, 'Passive Voice');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 4, 3, 5, '11:25:00', '12:10:00', '105', 'SEMINAR', 1, FALSE, 'Поэзия Серебряного века');
INSERT INTO lessons (group_id, subject_id, teacher_id, day_of_week, start_time, end_time, classroom, lesson_type, week_number, is_alternating_week, lesson_topic) VALUES (3, 13, 11, 5, '12:20:00', '13:05:00', 'Спортзал', 'PRACTICAL', 1, FALSE, 'Бasketball');

INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (1, 1, '10:10:00', '10:25:00', 'Перемена'), (1, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (1, 2, '10:10:00', '10:25:00', 'Перемена'), (1, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (1, 3, '10:10:00', '10:25:00', 'Перемена'), (1, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (1, 4, '10:10:00', '10:25:00', 'Перемена'), (1, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (1, 5, '10:10:00', '10:25:00', 'Перемена'), (1, 5, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (6, 1, '10:10:00', '10:25:00', 'Перемена'), (6, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (6, 2, '10:10:00', '10:25:00', 'Перемена'), (6, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (6, 3, '10:10:00', '10:25:00', 'Перемена'), (6, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (6, 4, '10:10:00', '10:25:00', 'Перемена'), (6, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (6, 5, '10:10:00', '10:25:00', 'Перемена'), (6, 5, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (7, 1, '10:10:00', '10:25:00', 'Перемена'), (7, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (7, 2, '10:10:00', '10:25:00', 'Перемена'), (7, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (7, 3, '10:10:00', '10:25:00', 'Перемена'), (7, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (7, 4, '10:10:00', '10:25:00', 'Перемена'), (7, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (7, 5, '10:10:00', '10:25:00', 'Перемена'), (7, 5, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (2, 1, '10:10:00', '10:25:00', 'Перемена'), (2, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (2, 2, '10:10:00', '10:25:00', 'Перемена'), (2, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (2, 3, '10:10:00', '10:25:00', 'Перемена'), (2, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (2, 4, '10:10:00', '10:25:00', 'Перемена'), (2, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (2, 5, '10:10:00', '10:25:00', 'Перемена'), (2, 5, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (4, 1, '10:10:00', '10:25:00', 'Перемена'), (4, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (4, 2, '10:10:00', '10:25:00', 'Перемена'), (4, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (4, 3, '10:10:00', '10:25:00', 'Перемена'), (4, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (4, 4, '10:10:00', '10:25:00', 'Перемена'), (4, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (4, 5, '10:10:00', '10:25:00', 'Перемена'), (4, 5, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (5, 1, '10:10:00', '10:25:00', 'Перемена'), (5, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (5, 2, '10:10:00', '10:25:00', 'Перемена'), (5, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (5, 3, '10:10:00', '10:25:00', 'Перемена'), (5, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (5, 4, '10:10:00', '10:25:00', 'Перемена'), (5, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (5, 5, '10:10:00', '10:25:00', 'Перемена'), (5, 5, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (3, 1, '10:10:00', '10:25:00', 'Перемена'), (3, 1, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (3, 2, '10:10:00', '10:25:00', 'Перемена'), (3, 2, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (3, 3, '10:10:00', '10:25:00', 'Перемена'), (3, 3, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (3, 4, '10:10:00', '10:25:00', 'Перемена'), (3, 4, '12:10:00', '12:20:00', 'Большая перемена');
INSERT INTO schedule_breaks (group_id, day_of_week, start_time, end_time, label) VALUES (3, 5, '10:10:00', '10:25:00', 'Перемена'), (3, 5, '12:10:00', '12:20:00', 'Большая перемена');

INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 1, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 1, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 1, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 6, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 6, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 6, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 7, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 7, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 7, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 2, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 2, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 2, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 4, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 4, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 4, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 5, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 5, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 5, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Решить задачи № 145–158', 'Выполнить и сдать учителю', 1, 1, 3, '2026-05-20', '2026-05-27', 'HOMEWORK', 10, '2026-05-20 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Написать изложение', 'Выполнить и сдать учителю', 3, 3, 3, '2026-05-21', '2026-05-28', 'HOMEWORK', 10, '2026-05-21 08:00:00');
INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at) VALUES ('Лабораторная работа № 4', 'Выполнить и сдать учителю', 5, 4, 3, '2026-05-22', '2026-05-29', 'HOMEWORK', 10, '2026-05-22 08:00:00');

DO $$
DECLARE
    s RECORD;
    subj RECORD;
    i INT;
    gval INT;
    gtype TEXT;
    lid INT;
BEGIN
    FOR s IN SELECT id, group_id FROM students ORDER BY group_id, id LOOP
        FOR subj IN
            SELECT DISTINCT l.subject_id, l.teacher_id, MIN(l.id) AS lesson_id
            FROM lessons l
            WHERE l.group_id = s.group_id
            GROUP BY l.subject_id, l.teacher_id
        LOOP
            FOR i IN 1..(3 + (s.id % 3)) LOOP
                gval := 3 + ((s.id + subj.subject_id + i) % 3);
                gtype := (ARRAY['CURRENT','HOMEWORK','TEST','INDEPENDENT'])[1 + ((s.id + i) % 4)];
                INSERT INTO grades (student_id, subject_id, teacher_id, value, grade_type,
                    comment, lesson_id, weight, created_at, updated_at)
                VALUES (s.id, subj.subject_id, subj.teacher_id, gval, gtype,
                    'Оценка за работу на уроке', subj.lesson_id, 1,
                    '2026-05-15 10:00:00', '2026-05-15 10:00:00');
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

DO $$
DECLARE
    d DATE;
    l RECORD;
    s RECORD;
    st TEXT;
BEGIN
    FOR d IN SELECT generate_series('2026-05-12'::date, '2026-05-23'::date, '1 day'::interval)::date LOOP
        IF EXTRACT(ISODOW FROM d) > 5 THEN CONTINUE; END IF;
        FOR l IN
            SELECT id, group_id, teacher_id, day_of_week, start_time
            FROM lessons
            WHERE day_of_week = EXTRACT(ISODOW FROM d)
        LOOP
            FOR s IN SELECT id FROM students WHERE group_id = l.group_id LOOP
                st := (ARRAY['PRESENT','PRESENT','PRESENT','PRESENT','LATE','ABSENT','EXCUSED_ABSENT'])
                      [1 + ((s.id + l.id + EXTRACT(DAY FROM d)::int) % 7)];
                INSERT INTO attendance (student_id, lesson_id, date, status, marked_at, marked_by)
                VALUES (s.id, l.id, d, st, d + l.start_time, l.teacher_id);
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

INSERT INTO homework_completions (assignment_id, student_id, status, submitted_at, received_points)
SELECT a.id, s.id,
    CASE WHEN (s.id + a.id) % 5 = 0 THEN 'ASSIGNED'
         WHEN (s.id + a.id) % 3 = 0 THEN 'GRADED'
         ELSE 'SUBMITTED' END,
    CASE WHEN (s.id + a.id) % 5 <> 0 THEN a.due_date + TIME '18:00:00' ELSE NULL END,
    CASE WHEN (s.id + a.id) % 3 = 0 AND (s.id + a.id) % 5 <> 0 THEN 6 + ((s.id + a.id) % 5) ELSE NULL END
FROM assignments a
JOIN students s ON s.group_id = a.group_id;

INSERT INTO notifications (user_id, title, message, notification_type, is_read, created_at)
SELECT s.user_id,
    (ARRAY['Новая оценка','Домашнее задание','Изменение расписания','Объявление'])[1 + (s.id % 4)],
    (ARRAY['Вы получили новую оценку','Проверьте домашнее задание','Урок перенесён','Родительское собрание 15 июня'])[1 + (s.id % 4)],
    (ARRAY['NEW_GRADE','HOMEWORK','SCHEDULE_CHANGE','ANNOUNCEMENT'])[1 + (s.id % 4)],
    (s.id % 3 = 0),
    '2026-05-28 09:00:00'
FROM students s;

COMMIT;
