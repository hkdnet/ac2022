require "set"
# s = File.read("input/6-0.txt")
s = File.read("input/6-1.txt")
s.split("").each_cons(4).each.with_index(0) do |a, index|
  s = Set.new
  a.each {|b| s << b}
  if s.size == 4
    puts index + 4
    exit 0
  end
end
