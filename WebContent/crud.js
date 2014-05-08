App = Ember.Application.create({
	LOG_TRANSITIONS: true
});

App.ApplicationAdapter = DS.RESTAdapter.extend({
	  namespace: 'EmberTest/emberbackend'
	});

//App.ApplicationAdapter = DS.FixtureAdapter;

App.IndexRoute = Ember.Route.extend({
	model: function(){
		return this.store.find('contact');
	},
	renderTemplate: function(){
		this.render('table', {outlet: 'contacts'});
		this.render('edit', {outlet: 'fields'});
	}
});

App.IndexController = Ember.ArrayController.extend({
	itemController: 'contact',
	selectedContact : '',
	editedContact : '',
	actions: {
 		newContact: function(){
 			var record=this.store.createRecord('contact', {
 				firstName: '',
 				lastName: '',
 				phone: '',
 				email: ''
 			});
 			record.save();
 		},
 		editContact: function(){
 			var self=this;
			var edited=App.ContactTemplate.create();
 			var promise=this.store.find('contact', this.selectedContact);
 			promise.then(function(contact){
 				edited.set('id',contact.get('id'));
 				edited.set('firstName',contact.get('firstName'));
 				edited.set('lastName',contact.get('lastName'));
 				edited.set('phone',contact.get('phone'));
 				edited.set('email',contact.get('email'));
 				self.set('editedContact',edited);
 			});
 		},
 		deleteContact: function(){
 			this.store.find('contact', this.selectedContact).then(function(contact){
 				contact.destroyRecord();
 			});
 			this.set('selectedContact','');
 		},
 		saveContact: function(){
 			var edited=this.get('editedContact');
 			var self=this;
 			this.store.find('contact', edited.get('id')).then(function(contact){
 				contact.set('firstName',edited.firstName);
 				contact.set('lastName',edited.lastName);
 				contact.set('phone',edited.phone);
 				contact.set('email', edited.email);
 				contact.save();
 				self.set('editedContact','');
 			});
 		},
		cancelEdit: function(){
			this.set('editedContact','');
		}
	}
});

App.ContactController = Ember.ObjectController.extend({
	needs:['index'],
	className: function(){
		var indexController=this.get('controllers.index');
		if(indexController.get('selectedContact')==this.get('id')){
			return 'selected';
		} else{
			return '';
		}
	}.property('controllers.index.selectedContact'),
	actions: {
		rowSelect: function(contact){
			this.get('controllers.index').set('selectedContact',this.get('id'));
	 	}
	}
});

App.ContactTemplate = Ember.Object.extend({
	id:0,
	firstName: '',
	lastName: '',
	phone: '',
	email: ''
});

App.Contact = DS.Model.extend({
	firstName: DS.attr('string'),
	lastName: DS.attr('string'),
	phone: DS.attr('string'),
	email: DS.attr('string')
});

//App.Contact.FIXTURES = [
//{
//	id: 1,
//	firstName: "Test",
//	lastName: "Person",
//	phone: "13224",
//	email: "test@example.com"
//},
//{
//	id: 2,
//	firstName: "Test",
//	lastName: "Person 2",
//	phone: "13224",
//	email: "test@example.com"
//}
//];
