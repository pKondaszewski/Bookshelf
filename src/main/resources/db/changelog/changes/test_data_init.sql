/* bookshelf database test data */

/* Starting books */
INSERT INTO public.book VALUES ('1', 'Andrzej Mickiewicz', 'wiersze', true, '3');
INSERT INTO public.book VALUES ('2', 'Golota Mickiewicz', 'walka', true, '3');
INSERT INTO public.book VALUES ('3', 'Wojciech Mickiewicz', 'testing', false, '3');

/* Starting borrows */
--INSERT INTO public.borrow VALUES ('1', 'Anna', 'Kot', '2015-11-11', '2015-12-12', 'random komentarz', '2');
--INSERT INTO public.borrow VALUES ('2', 'Anna', 'Kot', '2016-11-11', '2016-12-12', null, '2');
--INSERT INTO public.borrow VALUES ('3', 'Marian', 'Bąk', '2017-11-11', '2017-12-12', 'random komentarz 2', '2');
INSERT INTO public.borrow VALUES ('4', 'Marek', 'Kot', '2020-11-11', null, null, '3');