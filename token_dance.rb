#!/usr/bin/env ruby
require 'rubygems'
require 'twitter_oauth'

creds = YAML.load_file(".credentials")[:twitter]

client = TwitterOAuth::Client.new(
    :consumer_key => creds[:consumer_key],
    :consumer_secret => creds[:consumer_secret]
)

puts "Getting a request token..."
request_token = client.request_token()

puts "Navigate to this URL while logged in under whatever account you want:"
puts "  #{request_token.authorize_url}"
puts "\n\nEnter the PIN and press enter:"

pin = STDIN.gets.strip

puts "Authorizing..."

access_token = client.authorize(
  request_token.token,
  request_token.secret,
  :oauth_verifier => pin
)

puts "Your access token is: #{access_token.token}"
puts "Your access token secret is: #{access_token.secret}"

puts "You should be good to go!"