@echo off
rem Fordítás: az összes Java forrásfájl lefordítása a bin mappába
javac -d bin src/GameLogic/*.java src/GameMap/*.java src/Entity/*.java src/Interfaces/*.java src/Item/*.java src/ViewModels/*.java src/Views/*.java

rem Futás: az alkalmazás futtatása a GameLogic.Main fő osztályból
java -cp bin GameLogic.Main

rem Várakozás a felhasználói bemenetre, hogy a parancssor ne záródjon be azonnal
pause
