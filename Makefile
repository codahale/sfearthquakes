test:
	./sfearthquakes.rb -v

deploy:
	ssh codahale.com "cd ~/sfearthquakes && git pull"

token:
	./token_dance.rb

