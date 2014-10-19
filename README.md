## Бабуни на јаже

### 1. Проблем

  Некаде во националниот парк Кругер во Јужна Африка има голем кањон. Двете страни на кањонот ги спојува јаже. Бабуните можат да го преминат кањонот користејќи го јажето. Доколку два бабуни кои одат во спротивни насоки се сретнат тогаш ќе настане борба и двата бабуни ќе паднат од јажето. Јажето може да издржи највеќе пет бабуни во исто време. Ако има повеќе од пет бабуни во даден момент, јажето ќе се скине.
	
### 2. Барања
  Да претпоставиме дека може да ги научиме бабуните да користат семафори. Потребно е да се напише решение за синхронизација кое ќе ги задоволи следниве услови:
  - Откако еден бабун ќе се качи на јажето, тој треба да помине безбедно на другата страна без притоа да се сретне со некој друг бабун од спротивната насока.
  - На јажето во исто време може да има највеќе пет бабуни.
  - Група бабуни кои минуваат во една насока не треба да го спречат минувањето на бабуните од спортивна насока, т.е тие не треба да чекаат бесконечно. (Проблем на изгладнување)
  
	
### 3. Методи
  Да претпоставиме дека кањонот има две страни, лева и десна. Во решението мора да се искористат неколку методи кои ќе ја дефинираат состојбата.
  - `state.leftPassing()` и `state.rightPassing()` методите соодветно, се повикуваат за да се назначи дека јажето моментално ќе го користат само бабуните кои минуваат од соодветната страна. Доколку бабуните од лева страна го започнат нивното минување, првиот бабун од групата е должен да го повика методот `state.leftPassing()`. **Напомена**: Овој метод го повикува само првиот бабун од соодветната страна. Методот се повикува секогаш кога ќе настане променва во насоките на движењето.
  - `state.cross(this)` методот се повикува кај секој од бабуните (вклучувајќи го и првиот). Овој метод означува дека бабунот го започнал неговото изминување на јажето.
  - `state.leave(this)` методот се повкува кај секој од бабуните откако бабунот ќе премине на другата страна на каноњот.
