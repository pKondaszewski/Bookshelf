/* bookshelf database init file */

/* Starting categories */
INSERT INTO public.category VALUES ('1', 'Programming');
INSERT INTO public.category VALUES ('2', 'Management');
INSERT INTO public.category VALUES ('3', 'Testing');
INSERT INTO public.category VALUES ('4', 'Default');

/* Starting books */
INSERT INTO public.book VALUES ('1', 'Andrzej Mickiewicz', 'wiersze', true, '3');
INSERT INTO public.book VALUES ('2', 'Golota Mickiewicz', 'walka', true, '3');
INSERT INTO public.book VALUES ('3', 'Wojciech Mickiewicz', 'testing', false, '3');