UPDATE schedule_breaks SET label = 'Перемена' WHERE start_time = '10:10:00';
UPDATE schedule_breaks SET label = 'Большая перемена' WHERE start_time = '12:10:00';

UPDATE assignments SET title = 'Решить задачи № 145–158', description = 'Выполнить и сдать учителю'
WHERE MOD(id, 3) = 1;

UPDATE assignments SET title = 'Написать изложение', description = 'Выполнить и сдать учителю'
WHERE MOD(id, 3) = 2;

UPDATE assignments SET title = 'Лабораторная работа № 4', description = 'Выполнить и сдать учителю'
WHERE MOD(id, 3) = 0;