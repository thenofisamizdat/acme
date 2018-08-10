(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('answer', {
            parent: 'entity',
            url: '/answer',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Answers'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/answer/answers.html',
                    controller: 'AnswerController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('answer-detail', {
            parent: 'answer',
            url: '/answer/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Answer'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/answer/answer-detail.html',
                    controller: 'AnswerDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Answer', function($stateParams, Answer) {
                    return Answer.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'answer',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('answer-detail.edit', {
            parent: 'answer-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer/answer-dialog.html',
                    controller: 'AnswerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Answer', function(Answer) {
                            return Answer.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('answer.new', {
            parent: 'answer',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer/answer-dialog.html',
                    controller: 'AnswerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                answerText: null,
                                associatedQuestion: null,
                                associatedQuestionID: null,
                                userID: null,
                                answeredDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('answer', null, { reload: 'answer' });
                }, function() {
                    $state.go('answer');
                });
            }]
        })
        .state('answer.edit', {
            parent: 'answer',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer/answer-dialog.html',
                    controller: 'AnswerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Answer', function(Answer) {
                            return Answer.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('answer', null, { reload: 'answer' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('answer.delete', {
            parent: 'answer',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer/answer-delete-dialog.html',
                    controller: 'AnswerDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Answer', function(Answer) {
                            return Answer.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('answer', null, { reload: 'answer' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
