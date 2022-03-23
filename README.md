# Projekt przygotowujący "Biblioteka"

Use cases:
1. Dodanie nowej książki podając Autora, tytuł, katogrię 
2. Jeżeli dodajemy książkę bez kategorii, to książka powinna być dodawana do domyślnej kategorii. 
3. Dodanie nowej kategorii podając jej nazwę 
4. Startowe kategorie: Programming, Management, Testing, Default
5. Wypożyczanie książki podając autora i tytuł książki (drugi endpoint, zamiast autora i tytułu - id), imię i nazwisko wypożyczającego, datę wypożyczenia oraz opcjonalny komentarz. W przypadku braku podania daty, zapisywana powinna być data dzisiejsza. 
6. Wypożyczanie ma być robione w tranzakcji. 
7. Wypożyczona książka nie może być usunięta. 
8. Możliwość zwracania książki z wypożyczenia – w przypadku, gdy wypożyczenie się kończy, dodanie daty oddania książki. Jeżeli nie podamy daty, to ustawiamy dzisiejszą. 
9. Możliwość wylistowania książek dostępnych do wypożyczenia 
10. Możliwość wylistowania wypożyczonych książek wraz z wypożyczającym oraz datą wypożyczenia (oraz ewentualny komentarz)  
11. Wylistowanie ksiazek z punktu 10, mozliwosc sortowania po wypozyczajacym oraz po dacie. 
12. Możliwość wylistowania wszystkich książek, z dopiskiem które są wolne, a które są zajęte (przez kogo i od kiedy) 
13. Możliwość wylistowania kategorii wraz z liczbą książek do nich przypisanych. 
14. Możliwość wylistowania historii książki – pokazuje przeszłe wypożyczenia, oraz aktualne jeżeli jest. Jeżeli nie to pisze, że jest dostępna. 
15. Możliwość wylistowania historii wypożyczeń podając imię i nazwisko – wyświetla listę zakończonych wypożyczeń oraz aktualnie wypożyczone książki z wraz z podaniem ich liczby. 
16. Możliwość usuwania kategorii (bez starowych)
17. Jeżeli usuwamy kategorie, to przypisane do niej książki trafiają do defaultowej.
18. Możliwość usuwania książki
19. Możliwość usuwania określonego zakończonego wypożyczenia z historii książki 
20. W przypadku implementacji liquibase, zrob 2 setupy: czysty i testowy. Konfiguracja powinna byc w pliku konfiguracyjnym aplikacji. 


Technicznie: 
- Enpointy powinny zwracać odpowiednie kody HTTP
- W miejscach gdzie jest to uzasadnione zwracamy JSONa  
- Technologie: Spring Boot, Maven, jUnit, Mockito, PostgreSQL, GIT
- Dodatkowo: Swagger, liquibase
- GIT: Master branch powinien zawierac mozliwie jak najstabilniejsza wersje, oznacza to prace na develop branchu, plus ewentualne feature branche jezeli potrzebne. Uzywaj rebase (unikamy merge commitow!) i squash.
- Kod powinien być pokryty testami jednostkowymi (75% pokrycia)
- Testy jednostkowe uruchamiamy bez kontekstu Springowego
- Setup Guide – kod powienien być stworzony w taki sposób, aby osoba z zewnątrz mogła go pobrać i ustawić. Tyczy się to również bazy danych – przygotuj skrypty stawiające bazę, albo wykorzystaj liquibase. Na repo powinien znajdować się poradnik developerski opisujący jak postawić tą aplikację.
- JavaDoc – opisywanie kodu zgodnie z konwencja.
- Opisywanie commitow: 
- Opisuj każdy commit w sposób: Task-[NR] Opis wykonanej pracy 
 Dla pierwszego zadania z listy może to być np: 
 Task-1 Implementacja endpointa do dodawania nowej książk
