JAR=target/notes-calendar-1.0.0.jar

.PHONY: build run stop logs restart clean

build:
	@mvn -q clean package

run:
	@[ -f app.pid ] && kill `cat app.pid` 2>/dev/null || true
	@nohup java -jar $(JAR) > app.log 2>&1 & echo $$! > app.pid
	@sleep 4 && (grep -q "Tomcat started on port(s): 8080" app.log || grep -q "Started NotesCalendarApplication" app.log) && echo "✅ UP on 8080" || (echo "⚠️ Not up, tail:" && tail -n 120 app.log)

stop:
	@[ -f app.pid ] && kill `cat app.pid` 2>/dev/null && rm -f app.pid || true

logs:
	@tail -n 200 -f app.log

restart: build stop run

clean:
	@rm -f app.log app.pid
