package extr_ex.dialog.ex1;

public class Dish {
	private String _name;

	Dish(String name) {
		_name = name;
	}
	
	public String get_name() {
		return _name;
	}
	
	@Override
	public String toString() {
		return _name;
	}
}
