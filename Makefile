test:
	./sfearthquakes.rb -v -d

deploy:
	ssh codahale.com "cd ~/sfearthquakes && git pull"

token:
	./token_dance.rb

