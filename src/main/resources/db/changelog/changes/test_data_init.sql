/* bookshelf database test data */

/* Starting books */
INSERT INTO public.book VALUES ('1', 'Andrzej Mickiewicz', 'wiersze', true, '3');
INSERT INTO public.book VALUES ('2', 'Golota Mickiewicz', 'walka', true, '3');
INSERT INTO public.book VALUES ('3', 'Wojciech Mickiewicz', 'testing', false, '2');
INSERT INTO public.book VALUES ('4', 'Marian Mickiewicz', 'tytul', false, '3');
INSERT INTO public.book VALUES ('5', 'Michał Kowalczyk', 'raz dwa trzy', false, '3');
INSERT INTO public.book VALUES ('6', 'Piotr Oko', 'green book', false, '4');

/* Starting borrows */
INSERT INTO public.borrow VALUES ('1', 'Anna', 'Kot', '2015-11-11', '2015-12-12', 'random komentarz', '2');
INSERT INTO public.borrow VALUES ('2', 'Anna', 'Kot', '2016-11-11', '2016-12-12', null, '2');
INSERT INTO public.borrow VALUES ('3', 'Marian', 'Bąk', '2017-11-11', '2017-12-12', 'random komentarz 2', '2');
INSERT INTO public.borrow VALUES ('4', 'Marek', 'Kot', '2020-11-11', null, null, '3');
INSERT INTO public.borrow VALUES ('5', 'Anna', 'Kot', '2022-11-11', null, null, '4');
INSERT INTO public.borrow VALUES ('6', 'Janusz', 'Adamczyk', '2014-03-22', null, null, '5');
INSERT INTO public.borrow VALUES ('7', 'Ziemowit', 'Zielony', '2017-01-01', null, null, '6');