#!/usr/bin/env ruby
require 'rubygems'
require 'hpricot'
require 'twitter_oauth'
require 'grackle'
require 'yaml'
require 'bitly'

LAST_CHECKED_FILE = File.join(File.dirname(__FILE__), ".last_checked")
CREDENTIALS_FILE = File.join(File.dirname(__FILE__), ".credentials")

class Earthquake < Struct.new(:url, :location, :magnitude, :latitude, :longitude, :timestamp)
  EXPRESSIONS = [
    "Yawn.",          # > 0.0 Richter
    "Hmm.",           # > 1.0 Richter
    "Hey.",           # > 2.0 Richter
    "Whoah.",         # > 3.0 Richter
    "Damn.",          # > 4.0 Richter
    "DUDE.",          # > 5.0 Richter
    "HOLY CRAP.",     # > 6.0 Richter
    "WOW.",           # > 7.0 Richter
    "MY GOD.",        # > 8.0 Richter
    "GOODBYE."        # > 9.0 Richter
  ]
  
  def expression
    EXPRESSIONS[magnitude.floor]
  end
  
  def message(bitly)
    p bitly.shorten(url).methods.sort
    "#{expression} A #{magnitude} quake just happened #{location}: #{bitly.shorten(url).short_url}"
  end
  
  def noticable?
    magnitude >= 3.0
  end
end

class Scraper
  class ParsingError < Exception; end
  USGS_URL = "http://earthquake.usgs.gov/earthquakes/recenteqscanv/FaultMaps/San_Francisco_eqs.html"
  
  attr_reader :page, :quakes
  
  def initialize
    @page = Net::HTTP.get(URI.parse(USGS_URL))
    @doc = Hpricot(@page)
    @quakes = parse(@doc)
  end
  
private

  def parse(doc)
    quakes = []
  
    magnitudes = doc.search("td.magnitude").map { |td| td.inner_html.to_f }
    dates = doc.search("td.event_date").map { |td| td.inner_html }
    times = doc.search("td.event_time").map { |td| td.inner_html }
    latitudes = doc.search("td.latitude").map { |td| td.inner_html }
    longitudes = doc.search("td.longitude").map { |td| td.inner_html }
    links = doc.search("td.location a").map { |a| "http://earthquake.usgs.gov" + a["href"] }
    locations = doc.search("td.location a").map { |a| a.inner_html }
    
    if [magnitudes.size, dates.size, times.size, latitudes.size,
        longitudes.size, links.size, locations.size].uniq.size == 1 && magnitudes.size > 0
        
      data = magnitudes.zip(dates).zip(times).zip(latitudes).zip(longitudes).zip(links).zip(locations).map { |t| t.flatten }
      for (magnitude, date, time, latitude, longitude, link, location) in data
        timestamp = Time.parse("#{date} #{time} -800")
        
        quakes << Earthquake.new(link, location, magnitude, latitude, longitude, timestamp)
      end
    else
      raise ParsingError.new("Error parsing document:\n#{doc.inner_html}")
    end
    quakes
  end
  
end

if __FILE__ == $0
  verbose = ARGV.include?("-v")
  dry_run = ARGV.include?("-d")
  
  credentials = if File.exist?(CREDENTIALS_FILE)
    YAML.load_file(CREDENTIALS_FILE)
  else
    raise "Unable to find credentials: #{CREDENTIALS_FILE}"
  end
  
  scraper = Scraper.new
  if verbose
    puts "Found quakes:"
    p scraper.quakes
  end
  
  last_checked_at = if File.exist?(LAST_CHECKED_FILE)
    Time.parse(File.read(LAST_CHECKED_FILE))
  else
    Time.now - (60 * 60) # 1 hour ago
  end
  
  if verbose
    puts "Last checked at #{last_checked_at}"
  end
  
  new_quakes = scraper.quakes.select { |q| q.timestamp > last_checked_at && q.noticable? }.sort_by { |q| q.timestamp }
  
  if verbose
    puts "#{new_quakes.size} new quakes to tweet about"
  end
  
  
  
  client = Grackle::Client.new(
    :auth => credentials[:twitter].merge(:type => :oauth),
    :headers => {'User-Agent' => "SFEarthqakes/1.0 Grackle/#{Grackle::VERSION}"}
  )
  
  Bitly.use_api_version_3
  bitly = Bitly.new(credentials[:bitly][:username], credentials[:bitly][:api_key])
  
  for quake in new_quakes
    message = quake.message(bitly)
  
    if verbose
      puts "Tweeting: #{message}"
    end
    
    if !dry_run
      client.statuses.update!(:status => message)
    end
  end
  
  
  # update the last time we ran this
  File.open(LAST_CHECKED_FILE, "w") do |f|
    f << (new_quakes.last ? new_quakes.last.timestamp.to_s : Time.now.to_s)
  end
end