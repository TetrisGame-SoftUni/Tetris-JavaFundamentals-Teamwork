function solve(arr) {
	var numbers = arr.splice(1).map(Number);
	
	numbers.sort(function (a, b) { return a - b; });
	var smallest3Numbers = numbers.slice(0, 3);
	
	for (var i in smallest3Numbers) {
		console.log(smallest3Numbers[i]);
	}
}
