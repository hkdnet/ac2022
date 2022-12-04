def parse(s)
  s.split("-").map(&:to_i)
end

def full_cover?(a1, a2, b1, b2)
  (a1 <= b1 && b2 <= a2) || (b1 <= a1 && a2 <= b2)
end
def solve(s)
  assignments = s.split("\n").map do |line|
    s1, s2 = line.split(",", 2)
    a1, a2 = parse(s1)
    b1, b2 = parse(s2)
    [a1, a2, b1, b2]
  end

  cnt = 0
  assignments.each do |a|
    a1, a2, b1, b2 = a
    cnt += 1 if full_cover?(a1, a2, b1, b2)
  end
  cnt
end

puts solve(File.read("input/4-0.txt"))
puts solve(File.read("input/4-1.txt"))
