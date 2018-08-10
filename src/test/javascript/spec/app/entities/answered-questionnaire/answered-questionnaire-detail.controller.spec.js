'use strict';

describe('Controller Tests', function() {

    describe('AnsweredQuestionnaire Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockAnsweredQuestionnaire, MockAnswer;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockAnsweredQuestionnaire = jasmine.createSpy('MockAnsweredQuestionnaire');
            MockAnswer = jasmine.createSpy('MockAnswer');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'AnsweredQuestionnaire': MockAnsweredQuestionnaire,
                'Answer': MockAnswer
            };
            createController = function() {
                $injector.get('$controller')("AnsweredQuestionnaireDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'acmeApp:answeredQuestionnaireUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
