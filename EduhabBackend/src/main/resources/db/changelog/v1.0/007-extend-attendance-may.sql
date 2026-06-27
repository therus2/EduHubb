
DO $$
DECLARE
    d DATE;
    l RECORD;
    s RECORD;
    st TEXT;
BEGIN
    FOR d IN SELECT generate_series('2026-05-24'::date, '2026-05-31'::date, '1 day'::interval)::date LOOP
        IF EXTRACT(ISODOW FROM d) > 5 THEN CONTINUE; END IF;
        FOR l IN
            SELECT id, group_id, teacher_id, day_of_week, start_time
            FROM lessons
            WHERE day_of_week = EXTRACT(ISODOW FROM d)
        LOOP
            FOR s IN SELECT id FROM students WHERE group_id = l.group_id LOOP
                IF EXISTS (
                    SELECT 1 FROM attendance
                    WHERE student_id = s.id AND lesson_id = l.id AND date = d
                ) THEN
                    CONTINUE;
                END IF;
                st := (ARRAY['PRESENT','PRESENT','PRESENT','PRESENT','LATE','ABSENT','EXCUSED_ABSENT'])
                      [1 + ((s.id + l.id + EXTRACT(DAY FROM d)::int) % 7)];
                INSERT INTO attendance (student_id, lesson_id, date, status, marked_at, marked_by)
                VALUES (s.id, l.id, d, st, d + l.start_time, l.teacher_id);
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

INSERT INTO grades (student_id, subject_id, teacher_id, value, grade_type, comment, lesson_id, weight, created_at, updated_at)
SELECT s.id, l.subject_id, l.teacher_id,
       3 + ((s.id + l.subject_id) % 3),
       (ARRAY['CURRENT','HOMEWORK','TEST'])[1 + ((s.id + l.id) % 3)],
       'Оценка за работу на уроке',
       l.id, 1,
       '2026-05-27 12:00:00', '2026-05-27 12:00:00'
FROM students s
JOIN lessons l ON l.group_id = s.group_id AND l.day_of_week = 3
WHERE s.group_id = 1
  AND NOT EXISTS (
      SELECT 1 FROM grades g
      WHERE g.student_id = s.id AND g.lesson_id = l.id
        AND g.created_at >= '2026-05-27 00:00:00'
  );

INSERT INTO assignments (title, description, subject_id, teacher_id, group_id, assigned_date, due_date, assignment_type, max_points, created_at)
SELECT v.title, v.description, v.subject_id, l.teacher_id, 1, '2026-05-26', '2026-05-31', 'HOMEWORK', 10, '2026-05-26 08:00:00'
FROM (VALUES
    ('Подготовить доклад по теме', 'Краткий доклад на 5 минут', 4),
    ('Упражнения в учебнике §12', 'Выполнить письменно', 6),
    ('Контрольная работа — подготовка', 'Повторить пройденный материал', 2)
) AS v(title, description, subject_id)
JOIN lessons l ON l.group_id = 1 AND l.subject_id = v.subject_id
WHERE NOT EXISTS (
    SELECT 1 FROM assignments a
    WHERE a.group_id = 1 AND a.subject_id = v.subject_id AND a.due_date = '2026-05-31'
);

INSERT INTO homework_completions (assignment_id, student_id, status, submitted_at, received_points)
SELECT a.id, s.id,
    CASE
        WHEN (s.id + a.id) % 4 = 0 THEN 'ASSIGNED'
        WHEN (s.id + a.id) % 3 = 0 THEN 'GRADED'
        ELSE 'SUBMITTED'
    END,
    CASE WHEN (s.id + a.id) % 4 <> 0 THEN a.due_date + TIME '17:00:00' ELSE NULL END,
    CASE WHEN (s.id + a.id) % 3 = 0 AND (s.id + a.id) % 4 <> 0 THEN 7 + ((s.id + a.id) % 4) ELSE NULL END
FROM assignments a
JOIN students s ON s.group_id = a.group_id
WHERE a.group_id = 1
  AND a.assigned_date = '2026-05-26'
  AND NOT EXISTS (
      SELECT 1 FROM homework_completions hc
      WHERE hc.assignment_id = a.id AND hc.student_id = s.id
  );
